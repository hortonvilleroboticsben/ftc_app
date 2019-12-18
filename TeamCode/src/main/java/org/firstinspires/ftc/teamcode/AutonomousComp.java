package org.firstinspires.ftc.teamcode;


import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Autonomous (name = "Competition Autonomous", group = "Testing")
public class AutonomousComp extends OpMode {

    Robot r;
    StateMachine sm = new StateMachine();
    String AllianceColor = "Blue";
    boolean foundationSide, n  = false;
    double safeSpeed = .55;
    long wait = 0;
    boolean waitOS, confirmOS = false;
    Timer t = new Timer();

    @Override
    public void init() {
        r = Robot.getInstance();
        r.initialize(this);
    }

    @Override
    public void init_loop(){
        sm.initializeMachine();
        if(sm.next_state_to_execute()) {
            telemetry.addData("Foundation Side", "A For Yes : B For No");
            if ((gamepad1.a ^ gamepad1.b) && !gamepad1.start) {
                foundationSide = gamepad1.a;
                n = true;
            }
            if (n && !gamepad1.a && !gamepad1.b) {
                n = false;
                sm.incrementState();
            }
        }
        if(sm.next_state_to_execute()) {
            telemetry.addData("Alliance Color", "A For Red : B For Blue");
            if ((gamepad1.a ^ gamepad1.b) && !gamepad1.start) {
                AllianceColor = "Red";
                n = true;
            }
            if (n && !gamepad1.a && !gamepad1.b) {
                n = false;
                sm.incrementState();
            }
        }
        if(sm.next_state_to_execute()){
            telemetry.addData("Pause?","Dpad Down: -1 Second, Dpad Up: +1 Second");
            if(gamepad1.dpad_up && !waitOS){
                waitOS = true;
                wait+=1000;
            } else if (gamepad1.dpad_down && !waitOS){
                waitOS = true;
                wait-=1000;
            } else if (!gamepad1.dpad_up && !gamepad1.dpad_down){
                waitOS = false;
            } else if (gamepad1.a && !gamepad1.start && !confirmOS){
                confirmOS = true;
                sm.incrementState();
            } else if (!gamepad1.a){
                confirmOS = false;
            }
            telemetry.addData("Pause Amount:",wait);
        }
        telemetry.addData("Foundation Side", foundationSide+"");
    }

    @Override
    public void start(){
        r.setServoPosition("srvFlip",.2);
        sm.reset();
    }

    @Override
    public void loop() {
        sm.initializeMachine();
        sm.pause(wait);
            if (foundationSide) {
                sm.translate(90, safeSpeed, 24);
            } else {
                sm.translate(-90, safeSpeed, 39);
            }


        telemetry.addData("foundationSide", foundationSide+"");
        telemetry.addData("mtrLeftFront", r.getEncoderCounts("mtrFrontLeft"));
        telemetry.addData("mtrRightFront", r.getEncoderCounts("mtrRightFront"));
        telemetry.addData("mtrLeftBack", r.getEncoderCounts("mtrLeftBack"));
        telemetry.addData("mtrRightBack", r.getEncoderCounts("mtrRightBack"));
    }

    public void laps(double moreDistance){
        sm.translate(90, safeSpeed, 67+moreDistance);
        //Travel to Foundation and Place SkyStone
        sm.pause(500);
        sm.translate(-90, safeSpeed, 90+moreDistance);
        //Travel back for second stone
        sm.pause(500);
        //Grab second SkyStone
        sm.translate(90, safeSpeed, 90+moreDistance);
        //Travel to Foundation & Place Second SkyStone
        sm.pause(500);
        sm.translate(-90, safeSpeed, 40);
        //Park under bridge, ^ no need to change, 40 is same for all
    }
}
