package cv.app.base;


import cv.cloud.security.provider.imp.CvDefaultSessionProvider;
import cv.cloud.security.provider.spec.CvSessionProvider;
import cv.emp.admin.provider.imp.EmpApiAuthProvider;
import cv.service.security.service.ApplicationInitService;
import cv.tmp.maintenance.job.CvTmpMaintenanceSyncServerJob;
import xstartup.base.XContext;
import xstartup.base.XException;
import xstartup.base.XStartup;
import xstartup.base.conf.XServerConf;
import xstartup.base.conf.XServiceConf;
import xstartup.base.pattern.XProviderManager;
import xstartup.feature.*;
import xstartup.service.api.conf.XApiCookieConf;
import xstartup.service.api.conf.XApiServiceConf;
import xstartup.service.api.provider.XApiSystemProvider;
import xstartup.service.cgi.conf.XCgiServiceConf;
import xstartup.service.job.conf.XJobServiceConf;
import xstartup.service.rpc.conf.XRpcCenterConf;
import xstartup.springboot.XSpringbootStartup;

public class CvBaseApplication {
    public static void main(String[] args) throws XException {
        XStartup startup = new XSpringbootStartup();
        startup.setStartedEvent(CvBaseApplication::onStarted);
        startup.enable(XJpaFeature.class);
        startup.enable(XUiFeature.class);
        startup.enable(XRpcFeature.class)
                .config(new XRpcCenterConf("private", "127.0.0.1", 22059, true))
                .setHeartbeatEvent(CvBaseApplication::onHeartbeat);
        startup.enable(XJobFeature.class)
                .config(new XJobServiceConf(CvTmpMaintenanceSyncServerJob.class));
        startup.enable(XCgiFeature.class)
                .config(new XCgiServiceConf("cv"));
        startup.enable(XApiFeature.class)
                .config(new XApiServiceConf("cv"))
                .config(new XApiCookieConf("cv-app-base", "YUIDMP34**(319JJIyf1123f9ijn7ygv5yui", "8s%10m@k", 60000000));
        startup.config(new XServerConf(22054))
                .config(new XServiceConf("cv"));
        startup.run("cv-app-base", "cv-app-base", "xstarup", "xstarup", args);
    }

    private static void onHeartbeat(XContext context) throws Exception {
        ApplicationInitService bmpInitService = context.getBean(ApplicationInitService.class);
        bmpInitService.init(context).throwIfException();
    }

    private static void onStarted(XContext context) throws XException {
        XProviderManager.setDefaultImplement(XApiSystemProvider.class, EmpApiAuthProvider.class);
        XProviderManager.setDefaultImplement(CvSessionProvider.class, CvDefaultSessionProvider.class);
    }
}