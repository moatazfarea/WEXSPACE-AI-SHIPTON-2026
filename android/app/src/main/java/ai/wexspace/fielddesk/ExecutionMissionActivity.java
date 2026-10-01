package ai.wexspace.fielddesk;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public abstract class ExecutionMissionActivity extends Activity {
    private int stage=0;
    private LinearLayout trace;
    private TextView gate;
    private Button next;

    protected abstract String missionNumber();
    protected abstract String lane();
    protected abstract String missionName();
    protected abstract String requestSummary();
    protected abstract String specialistSummary();
    protected abstract String calculationSummary();
    protected abstract String verificationSummary();
    protected abstract boolean verificationPass();
    protected abstract String evidenceId();

    @Override protected void onCreate(Bundle state){
        super.onCreate(state);
        setContentView(build());
    }

    private ScrollView build(){
        ScrollView scroll=new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(WexspaceBrand.BG);

        LinearLayout root=new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20),dp(24),dp(20),dp(46));
        scroll.addView(root);

        root.addView(text("←  PROJECT "+missionNumber()+" / "+lane(),11,true,WexspaceBrand.CYAN));
        root.addView(text(missionName(),32,true,WexspaceBrand.TEXT));

        TextView ribbon=text("REQUEST  →  SPECIALIST  →  TOOL  →  VERIFY  →  EVIDENCE",11,true,WexspaceBrand.ICE);
        ribbon.setPadding(0,dp(10),0,dp(18));
        root.addView(ribbon);

        LinearLayout req=card();
        req.addView(tag("HUMAN-APPROVED REQUEST"));
        req.addView(text(requestSummary(),16,true,WexspaceBrand.TEXT));
        req.addView(text("No result exists yet. WEXSPACE advances only when each execution stage is completed.",12,false,WexspaceBrand.MUTED));
        root.addView(req,margin(dp(12)));

        trace=new LinearLayout(this);
        trace.setOrientation(LinearLayout.VERTICAL);
        root.addView(trace);

        gate=tag("STATE · READY FOR SPECIALIST ROUTING");
        gate.setTextColor(Color.rgb(255,208,102));
        gate.setPadding(0,dp(8),0,dp(10));
        root.addView(gate);

        next=button("ROUTE TO SPECIALIST");
        next.setOnClickListener(v->advance());
        root.addView(next);

        TextView authority=text("Human Authority Boundary · configured ≠ authorized ≠ executed ≠ verified",11,false,WexspaceBrand.MUTED);
        authority.setPadding(0,dp(20),0,0);
        root.addView(authority);
        return scroll;
    }

    private void advance(){
        stage++;
        if(stage==1){
            addReceipt("SPECIALIST ROUTED",specialistSummary(),WexspaceBrand.CYAN);
            gate.setText("STATE · READY FOR DETERMINISTIC TOOL");
            next.setText("RUN DETERMINISTIC TOOL");
        } else if(stage==2){
            addReceipt("TOOL EXECUTION",calculationSummary(),WexspaceBrand.ICE);
            gate.setText("STATE · EXECUTED / NOT YET VERIFIED");
            next.setText("RUN INDEPENDENT VERIFICATION");
        } else if(stage==3){
            addReceipt("INDEPENDENT VERIFIER",verificationSummary(),verificationPass()?WexspaceBrand.GREEN:Color.RED);
            gate.setText(verificationPass()?"STATE · VERIFIED / READY FOR EVIDENCE":"STATE · REVIEW REQUIRED");
            next.setText(verificationPass()?"ISSUE EVIDENCE RECEIPT":"RETURN TO HUMAN REVIEW");
            if(!verificationPass()) next.setEnabled(false);
        } else {
            addReceipt("EVIDENCE RECEIPT",evidenceId()+"
Hash-bound result package · provenance complete",WexspaceBrand.GREEN);
            gate.setText("STATE · VERIFIED + EVIDENCE-LINKED");
            next.setText("PROJECT COMPLETE");
            next.setEnabled(false);
        }
    }

    private void addReceipt(String title,String body,int color){
        LinearLayout c=card();
        c.addView(text("✓  "+title,11,true,color));
        TextView b=text(body,14,false,WexspaceBrand.TEXT);
        b.setPadding(0,dp(7),0,0);
        c.addView(b);
        c.setAlpha(0f);
        c.setTranslationY(dp(24));
        trace.addView(c,margin(dp(10)));
        c.animate().alpha(1f).translationY(0f).setDuration(460).start();
    }

    private LinearLayout card(){
        LinearLayout l=new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setPadding(dp(16),dp(15),dp(16),dp(15));
        l.setBackground(WexspaceBrand.panel(18,getResources().getDisplayMetrics().density));
        WexspaceBrand.elevate(l,3);
        return l;
    }
    private TextView tag(String s){TextView v=text(s,10,true,WexspaceBrand.CYAN);return v;}
    private Button button(String s){Button b=new Button(this);b.setText(s);b.setAllCaps(false);b.setTextColor(WexspaceBrand.TEXT);b.setBackground(WexspaceBrand.pill(Color.rgb(34,211,238),Color.rgb(7,37,61),12,getResources().getDisplayMetrics().density));return b;}
    private TextView text(String s,int sp,boolean bold,int color){TextView v=new TextView(this);v.setText(s);v.setTextSize(sp);v.setTextColor(color);if(bold)v.setTypeface(null,android.graphics.Typeface.BOLD);return v;}
    private LinearLayout.LayoutParams margin(int bottom){LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);p.setMargins(0,0,0,bottom);return p;}
    protected int dp(int v){return Math.round(v*getResources().getDisplayMetrics().density);}
}
