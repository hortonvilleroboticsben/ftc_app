package org.firstinspires.ftc.teamcode;


import android.os.Environment;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.ArrayList;

@Autonomous (name = "Competition Autonomous", group = "Testing")
public class AutonomousComp extends OpMode {

    Robot r;
    StateMachine sm = new StateMachine();
    StateMachine vision = new StateMachine();
    boolean OSFound,OSColor = false;
    double safeSpeed = .55;
    boolean waitOS, confirmOS = false;
    boolean selOS = false;
    Timer t = new Timer();

    JSONObject settings;
    File rFolder;
    ArrayList<String> fList = new ArrayList<>();
    int fIndex = 0;

//    String allianceColor, returnPath, apFoundationOrientation = "No Change";
    String allianceColor = "blue"; //default
    String returnPath = "wall"; //default
    String apFoundationOrientation = "|"; //default
    boolean loadingSideStart = true; //default
    boolean apMoveFoundation = false; //default
    long wait = 0; //default
    int skyStones = 2; //default

    int[] vsData = null;

    @Override
    public void init() {
        r = Robot.getInstance();
        r.initialize(this);

        rFolder = new File(Environment.getExternalStorageDirectory()+"/JSONConfigs");
        if(!rFolder.exists())
            rFolder.mkdir();
        for(File f : rFolder.listFiles()){
            if(f.getName().substring(f.getName().length()-5).equals(".json")) fList.add(f.getName());
        }

    }

    @Override
    public void init_loop(){
        sm.initializeMachine();
        //Replac e these preview questions with a JSON file with the pre-sets
        if(!confirmOS){
            if(fList.size() != 0) {
                telemetry.addData("Please select a file", fList.get(fIndex));

                if (gamepad1.dpad_down && !selOS) {
                    fIndex++;
                    selOS = true;
                }
                if (gamepad1.dpad_up && !selOS) {
                    fIndex--;
                    selOS = true;
                }
                if (!gamepad1.dpad_up && !gamepad1.dpad_down) selOS = false;

                fIndex = fIndex > fList.size() - 1 ? fList.size() - 1 : fIndex < 0 ? 0 : fIndex;

                if (gamepad1.a && !gamepad1.start && !gamepad2.start) confirmOS = true;
            }else{
                telemetry.addData("ERROR",  "No files available.");
            }
        } else {
            File rPath = new File(rFolder, fList.get(fIndex));
            try {
                InputStream fin = new FileInputStream(rPath);
                byte[] raw_inp = new byte[fin.available()];
                fin.read(raw_inp);
                String om = new String(raw_inp);
                settings = new JSONObject(om);
                allianceColor = settings.getString("alliance");
                loadingSideStart = settings.getBoolean("loadingSideStart");
                wait = settings.getLong("pauseTime");
                returnPath = settings.getString("returnPath");
                skyStones = settings.getInt("skyStones");
                apMoveFoundation = settings.getBoolean("apMoveFoundation");
                apFoundationOrientation = settings.getString("apFoundationOrientation");
            } catch (Exception e) {
                e.printStackTrace();
                telemetry.addData("TRY CATCH ERROR", e.getCause());
            }

        }
    }

    @Override
    public void start(){
        sm.reset();
    }
    @Override
    public void loop() {
        sm.initializeMachine();
        vision.initializeMachine();
//
        int[] temp = vision.getVisionData();
        vsData = temp == null ? vsData : temp;
        final int placement = vsData[0];
        telemetry.addData("Placement: ", placement+"");

        vision.SetFlag(sm, "Vision Done");


        sm.pause(wait);

        vision.WaitForFlag("VisionDone");

            if (loadingSideStart) {
                if (allianceColor.equals("blue")) {

                    sm.translate(90, safeSpeed, 26);
                    sm.rotate(-90, safeSpeed);

                    if (returnPath.equals("not_wall")) {

                        switch (placement) {

                            case 1:

                                sm.translate(-170, safeSpeed, 7);

                                // INSERT SERVO & LIFT CODE

//                                sm.translate(180, safeSpeed, 20.75);

                                //INSERT SERVO & LIFT CODE

                                sm.translate(0, safeSpeed, 12);

                                sm.translate(-90, safeSpeed, 69); //TO BUILDING SIDE

                                sm.translate(180, safeSpeed, 14); // DRIVE TO FOUNDATION

                                //INSERT SERVO FOUNDATION GRABBER CODE

                                sm.translate(0, safeSpeed, 5);
                                break;

                            case 2:
                                break;

                            case 3:
                                break;

                        }
                    } else { //returnPath.equals("wall");
                        switch (placement) {

                            case 1:

                                sm.translate(-165, safeSpeed, 7);

                                // INSERT SERVO & LIFT CODE

                                sm.translate(0, safeSpeed, 27);

                                sm.translate(-90, safeSpeed, 69); //TO BUILDING SIDE

                                sm.translate(180, safeSpeed, 24); //DRIVE TO FOUNDATION

                                break;
                            case 2:
                                sm.translate(170, safeSpeed, 7);

                                // INSERT SERVO & LIFT CODE

                                sm.translate(0, safeSpeed, 27);

                                sm.translate(-90, safeSpeed, 73); //TO BUILDING SIDE

                                sm.translate(180, safeSpeed, 24); //DRIVE TO FOUNDATION
                                break;

                            case 3:
                                sm.translate(110, safeSpeed, 12);

                                // INSERT SERVO & LIFT CODE

                                sm.translate(0, safeSpeed, 25);

                                sm.translate(-90, safeSpeed, 78); //TO BUILDING SIDE

                                sm.translate(180, safeSpeed, 24); //DRIVE TO FOUNDATION
                                break;

                        }
//                        if(sm.next_state_to_execute()){
//                            r.initRunToTarget("mtrLift", 3000, safeSpeed);
//                            if(r.hasMotorEncoderReached("mtrLift",3010)){
//                                r.initRunToTarget("mtrLift", 0, safeSpeed);
//                                if(r.hasMotorEncoderReached("mtrLift",0+200)){
//                                    r.setPower("mtrLift",0);
//                                }
//                            }
//                            r.setServoPosition("srvClampLeft", .1);
//                            r.setServoPosition("srvClampRight", .6);
//
//                            sm.translate(180, .2, 4);
//
//                            r.setServoPosition("srvClampRight", .1);
//
//
//                            sm.translate(0, .2, 4);
//
//                            sm.incrementState();
//                        }

//                        sm.translate(90, safeSpeed, 24); //travelling to wall
                    }

                    //BOTH DO AFTER REACHING FOUNDATION

                    sm.rotate(-90, safeSpeed);

                    //FOUNDATION GRABBER DOWN

                    sm.translate(0, safeSpeed, 23);
                    sm.translate(90, safeSpeed, 32);

                    //FOUNDATION GRABBER UP

//

//                    when have working color sensors

//                    if(sm.next_state_to_execute()) {
//                        int frontBlue = r.getColorValue("colorFront", "blue");
//                        int backBlue = r.getColorValue("colorBack", "blue");
//                        if(frontBlue<11){
//                            //WheelSetL is static, might cause problem
//                            r.setPower(Robot.wheelSet1[0],.2);
//                            r.setPower(Robot.wheelSet2[0],-.2);
//                        }else{
//                            r.setPower(Robot.wheelSet1[0],-.3);
//                            r.setPower(Robot.wheelSet2[0],.3);
//                        }
//                        if(backBlue<11){
//                            r.setPower(Robot.wheelSet1[1],.2);
//                            r.setPower(Robot.wheelSet2[1],-.2);
//                        }else{
//                            r.setPower(Robot.wheelSet1[1],-.3);
//                            r.setPower(Robot.wheelSet2[1],.3);
//                        }
//                        if(frontBlue >= 11 && backBlue >= 11){
//                            r.setDrivePower(0,0);
//                            sm.incrementState();
//                        }
//                    }


                    if(skyStones==2) { //GOING FOR SECOND SKYSTONE

                        if(returnPath.equals("wall")){

                            sm.translate(180, safeSpeed, 96);
                            sm.translate(-90, safeSpeed, 5);
                            sm.rotate(90, safeSpeed);

                            switch (placement){

                                case 1:
                                    break;

                                case 2:
                                    break;

                                case 3:
                                    break;

                            }

                            sm.translate(180, safeSpeed, 25);

                        }

                    } else { //ONE SKYSTONE & PARK UNDER BRIDGE


                    }

                } else { //ALLIANCE COLOR === RED

                }
            } else {
                if(allianceColor.equals("blue")){

                    sm.translate(90, safeSpeed, 26);
                    sm.rotate(-90, safeSpeed);

                    if(returnPath.equals("not_wall")){

                    } else { //returnPath.equals("wall");

                    }

                } else { // ALLIANCE COLOR === RED

                }
            }
        telemetry.addData("mtrLeftFront", r.getEncoderCounts("mtrFrontLeft"));
        telemetry.addData("mtrRightFront", r.getEncoderCounts("mtrRightFront"));
        telemetry.addData("mtrLeftBack", r.getEncoderCounts("mtrLeftBack"));
        telemetry.addData("mtrRightBack", r.getEncoderCounts("mtrRightBack"));

        telemetry.addData("RED Front", r.getColorValue("colorFront","red"));
        telemetry.addData("BLUE", r.getColorValue("colorFront","blue"));
        telemetry.addData("GREEN", r.getColorValue("colorFront","green"));

        telemetry.addData("RED Back", r.getColorValue("colorBack","red"));
        telemetry.addData("BLUE", r.getColorValue("colorBack","blue"));
        telemetry.addData("GREEN", r.getColorValue("colorBack","green"));
    }
}
