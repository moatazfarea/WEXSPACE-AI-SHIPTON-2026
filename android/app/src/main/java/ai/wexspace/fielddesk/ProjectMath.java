package ai.wexspace.fielddesk;

public final class ProjectMath {
    private ProjectMath() {}

    public static final class PumpResult {
        public final double velocity, reynolds, friction, losses, tdh, shaftKw, verificationDeltaPct;
        public final boolean pass;
        PumpResult(double velocity,double reynolds,double friction,double losses,double tdh,double shaftKw,double verificationDeltaPct,boolean pass){
            this.velocity=velocity; this.reynolds=reynolds; this.friction=friction; this.losses=losses;
            this.tdh=tdh; this.shaftKw=shaftKw; this.verificationDeltaPct=verificationDeltaPct; this.pass=pass;
        }
    }

    public static PumpResult pump(double flowM3h,double diameterM,double lengthM,double staticM,double minorK,double efficiency){
        final double rho=997.0, mu=0.00089, g=9.80665, rough=0.000045;
        double q=flowM3h/3600.0;
        double area=Math.PI*diameterM*diameterM/4.0;
        double v=q/area;
        double re=rho*v*diameterM/mu;
        double f=0.25/Math.pow(Math.log10(rough/(3.7*diameterM)+5.74/Math.pow(re,0.9)),2);
        double loss=(f*(lengthM/diameterM)+minorK)*(v*v/(2*g));
        double tdh=staticM+loss;
        double kw=rho*g*q*tdh/(Math.max(efficiency,0.01)*1000.0);

        double fh=1.0/Math.pow(-1.8*Math.log10(Math.pow(rough/(3.7*diameterM),1.11)+6.9/re),2);
        double tdh2=staticM+(fh*(lengthM/diameterM)+minorK)*(v*v/(2*g));
        double delta=Math.abs(tdh2-tdh)/Math.max(tdh,1e-9)*100.0;
        return new PumpResult(v,re,f,loss,tdh,kw,delta,delta<=5.0);
    }

    public static final class SolarResult {
        public final double pvKw,batteryKwh,inverterKw,annualKwh,dayCoverage;
        public final boolean pass;
        SolarResult(double pvKw,double batteryKwh,double inverterKw,double annualKwh,double dayCoverage,boolean pass){
            this.pvKw=pvKw; this.batteryKwh=batteryKwh; this.inverterKw=inverterKw;
            this.annualKwh=annualKwh; this.dayCoverage=dayCoverage; this.pass=pass;
        }
    }

    public static SolarResult solar(double dailyKwh,double peakKw,double psh,double derate,double autonomy,double dod,double roundTrip){
        double pv=dailyKwh/(psh*derate);
        double battery=dailyKwh*autonomy/(dod*roundTrip);
        double inverter=Math.max(peakKw*1.25,1.0);
        double annual=pv*psh*365.0*derate;
        double coverage=(battery*dod*roundTrip)/dailyKwh;
        boolean pass=pv*psh*derate>=dailyKwh*0.999 && coverage>=autonomy*0.999;
        return new SolarResult(pv,battery,inverter,annual,coverage,pass);
    }

    public static final class RescueResult {
        public final double usableKwh,batteryHours,solarEnergyKwh,totalHours;
        public final boolean pass;
        RescueResult(double usableKwh,double batteryHours,double solarEnergyKwh,double totalHours,boolean pass){
            this.usableKwh=usableKwh; this.batteryHours=batteryHours; this.solarEnergyKwh=solarEnergyKwh; this.totalHours=totalHours; this.pass=pass;
        }
    }

    public static RescueResult rescue(double batteryKwh,double soc,double reserve,double criticalW,double solarW,double sunHours){
        double usable=batteryKwh*Math.max(0,soc-reserve);
        double batteryHours=usable/(criticalW/1000.0);
        double solarEnergy=solarW*sunHours/1000.0;
        double totalHours=(usable+solarEnergy)/(criticalW/1000.0);
        return new RescueResult(usable,batteryHours,solarEnergy,totalHours,totalHours>=10.0);
    }
}
