package ai.wexspace.fielddesk;

import java.util.Locale;

public final class PumpSkidActivity extends ExecutionMissionActivity {
    private final ProjectMath.PumpResult r=ProjectMath.pump(18.0,0.065,85.0,24.0,6.0,0.68);
    protected String missionNumber(){return "01";}
    protected String lane(){return "MECHANICAL ENGINEERING";}
    protected String missionName(){return "Compact Pump Skid\nDuty-Point Design";}
    protected String requestSummary(){return "18 m³/h · 24 m static lift · 85 m run · Ø65 mm · K=6 · η=68%";}
    protected String specialistSummary(){return "Mechanical specialist accepted the duty point, normalized units, and selected a pressurized-water hydraulic calculation route.";}
    protected String calculationSummary(){return "Velocity "+f(r.velocity)+" m/s\nRe "+String.format(Locale.US,"%.0f",r.reynolds)+"\nSwamee–Jain f "+String.format(Locale.US,"%.4f",r.friction)+"\nLosses "+f(r.losses)+" m\nTDH "+f(r.tdh)+" m\nShaft power "+f(r.shaftKw)+" kW → 4.0 kW motor candidate";}
    protected String verificationSummary(){return "Haaland recomputation delta "+f(r.verificationDeltaPct)+"%\nAcceptance: ≤ 5% difference";}
    protected boolean verificationPass(){return r.pass;}
    protected String evidenceId(){return "PUMP-SKID-001";}
    private String f(double v){return String.format(Locale.US,"%.2f",v);}
}
