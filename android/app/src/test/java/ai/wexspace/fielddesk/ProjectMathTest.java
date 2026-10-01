package ai.wexspace.fielddesk;

import org.junit.Test;
import static org.junit.Assert.*;

public class ProjectMathTest {
    @Test public void pump_isPhysicallyConsistent(){
        ProjectMath.PumpResult r=ProjectMath.pump(18.0,0.065,85.0,24.0,6.0,0.68);
        assertTrue(r.velocity>1.0 && r.velocity<2.0);
        assertTrue(r.tdh>24.0);
        assertTrue(r.shaftKw>0.0);
        assertTrue(r.pass);
    }

    @Test public void solar_balancesDemandAndStorage(){
        ProjectMath.SolarResult r=ProjectMath.solar(9.6,2.2,5.8,0.78,1.5,0.80,0.90);
        assertTrue(r.pvKw>2.0);
        assertTrue(r.batteryKwh>15.0);
        assertTrue(r.dayCoverage>=1.5);
        assertTrue(r.pass);
    }

    @Test public void rescue_preservesOvernightWindow(){
        ProjectMath.RescueResult r=ProjectMath.rescue(2.4,0.40,0.20,120,600,5.5);
        assertTrue(r.totalHours>=10.0);
        assertTrue(r.pass);
    }
}
