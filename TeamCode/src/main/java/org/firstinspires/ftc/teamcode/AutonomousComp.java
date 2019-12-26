package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Autonomous (name = "Competition Autonomous", group = "Testing")
public class AutonomousComp extends OpMode {

    Robot r;
    StateMachine sm = new StateMachine();
    String AllianceColor = "Blue";
    boolean foundationSide, OSFound,OSColor = false;
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
                OSFound = true;
            }
            if (OSFound && !gamepad1.a && !gamepad1.b) {
                OSFound = false;
                sm.incrementState();
            }
        }

        if(sm.next_state_to_execute()) {
            telemetry.addData("Alliance Color:", "A For Red : B For Blue");
            if ((gamepad1.a ^ gamepad1.b) && !gamepad1.start) {
                AllianceColor = gamepad1.a ? "Red" : "Blue";
                OSColor = true;
            }
            if (OSColor && !gamepad1.a && !gamepad1.b) {
                OSColor = false;
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
        }
        telemetry.addData("Foundation Side", foundationSide+"");
        telemetry.addData("Alliance Color:", AllianceColor);
        telemetry.addData("Pause Amount:",wait);
    }

    @Override
    public void start(){
        sm.reset();
    }

    @Override
    public void loop() {
        sm.initializeMachine();
        sm.pause(wait);
            if (foundationSide) {
                sm.translate(0, safeSpeed, 5);
            } else {
                if(AllianceColor.equals("Blue")) {
                    sm.translate(0, safeSpeed, 36.75);
                    sm.translate(-90, safeSpeed, 3.6);
                    sm.rotate(-90, safeSpeed);
                    sm.translate(0, safeSpeed, 7);
                    //Insert Collection System
                    sm.translate(180, safeSpeed, 7);
                    sm.translate(-90, safeSpeed, 20);
                    sm.translate(180, 1.0, 76);
                    sm.rotate(90, safeSpeed);
                    //sm.translate(0,safeSpeed,4); Add this if our servos cant pick up at that distance
                    //Insert Foundation Servos
                    sm.translate(180, safeSpeed, 22.5);
                    //Pulled Foundation to Building Site ^

                    //Go back for Second SkyStone V------------

                    //If our alliance is not parked against wall
                    sm.translate(-90, .7, 117); //Add color sensor, to know when we're near the tape
                    //When see tape, move x amount to the 3rd Skystone

                    sm.translate(0, safeSpeed / 2, 15);
                    sm.translate(90, safeSpeed / 2, 10);
                    //sm.translate(-90, safeSpeed / 2, 3);

                    sm.translate(0, safeSpeed / 2, 10);
                    sm.translate(-90, safeSpeed / 2, 3.5);
                    sm.rotate(-90, safeSpeed / 2);
                    sm.translate(0, safeSpeed / 2, 7);
                    //Insert Collection System
                    sm.translate(180, safeSpeed / 2, 7);
                    sm.translate(-90, safeSpeed / 2, 20);
                    sm.translate(180, safeSpeed / 2, 77);
                } else {
                    sm.translate(0, safeSpeed, 36.75);
                    sm.translate(90, safeSpeed, 3.6);
                    sm.rotate(90, safeSpeed);
                    sm.translate(0, safeSpeed, 7);
                    //Insert Collection System
                    sm.translate(180, safeSpeed, 7);
                    sm.translate(90, safeSpeed, 20);
                    sm.translate(180, 1.0, 72);
                    sm.rotate(-90, safeSpeed);
                    //sm.translate(0,safeSpeed,4); Add this if our servos cant pick up at that distance
                    //Insert Foundation Servos
                    sm.translate(180, .6, 22);
                    //Pulled Foundation to Building Site ^

                    //Go back for Second SkyStone V------------

                    //If our alliance is not parked against wall
                    sm.translate(90, .7, 111); //Add color sensor, to know when we're near the tape
                    //When see tape, move x amount to the 3rd Skystone
                }
            }
        telemetry.addData("mtrLeftFront", r.getEncoderCounts("mtrFrontLeft"));
        telemetry.addData("mtrRightFront", r.getEncoderCounts("mtrRightFront"));
        telemetry.addData("mtrLeftBack", r.getEncoderCounts("mtrLeftBack"));
        telemetry.addData("mtrRightBack", r.getEncoderCounts("mtrRightBack"));
    }

//    public void laps(double moreDistance){
//        sm.translate(90, safeSpeed, 67+moreDistance);
//        //Travel to Foundation and Place SkyStone
//        sm.pause(500);
//        sm.translate(-90, safeSpeed, 90+moreDistance);
//        //Travel back for second stone
//        sm.pause(500);
//        //Grab second SkyStone
//        sm.translate(90, safeSpeed, 90+moreDistance);
//        //Travel to Foundation & Place Second SkyStone
//        sm.pause(500);
//        sm.translate(-90, safeSpeed, 40);
//        //Park under bridge, ^ no need to change, 40 is same for all
//    }
}
