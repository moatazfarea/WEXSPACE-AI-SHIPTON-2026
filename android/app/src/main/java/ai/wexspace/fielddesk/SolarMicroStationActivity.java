package ai.wexspace.fielddesk;

import java.util.Locale;

public final class SolarMicroStationActivity extends ExecutionMissionActivity {
    private final ProjectMath.SolarResult r=ProjectMath.solar(9.6,2.2,5.8,0.78,1.5,0.80,0.90);
    protected String missionNumber(){return "02";}
    protected String lane(){return "ENERGY ENGINEERING";}
    protected String missionName(){return "Mini Solar\nPower Station";}
    protected String requestSummary(){return "9.6 kWh/day · 2.2 kW peak · 5.8 PSH · 1.5-day autonomy";}
    protected String specialistSummary(){return "Energy specialist normalized load, solar resource, derating, depth-of-discharge, and storage-efficiency assumptions.";}
    protected String calculationSummary(){return "PV array "+f(r.pvKw)+" kWp\nBattery "+f(r.batteryKwh)+" kWh\nInverter "+f(r.inverterKw)+" kW\nExpected annual yield "+f(r.annualKwh)+" kWh";}
    protected String verificationSummary(){return "Usable battery coverage "+f(r.dayCoverage)+" days\nPV energy balance recomputed from independent demand equation";}
    protected boolean verificationPass(){return r.pass;}
    protected String evidenceId(){return "SOLAR-MICRO-001";}
    private String f(double v){return String.format(Locale.US,"%.2f",v);}
}
