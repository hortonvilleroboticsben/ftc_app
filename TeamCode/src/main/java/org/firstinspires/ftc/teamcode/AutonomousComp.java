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
    boolean OSFound,OSColor = false;
    double safeSpeed = .55;
    boolean waitOS, confirmOS = false;
    boolean selOS = false;
    Timer t = new Timer();

    JSONObject settings;
    File rFolder;
    ArrayList<String> fList = new ArrayList<>();
    int fIndex = 0;

    String allianceColor, returnPath, apFoundationOrientation = "No Change";
    boolean loadingSideStart, apMoveFoundation = false;
    long wait = 0;
    int skyStones = -1;

    @Override
    public void init() {
        r = Robot.getInstance();
        r.initialize(this);

        rFolder = new File(Environment.getExternalStorageDirectory()+"/JSONConfigs/");
        if(!rFolder.exists()) rFolder.mkdir();
        for(File f : rFolder.listFiles()){
            if(f.getName().substring(f.getName().length()-5).equals(".json")) fList.add(f.getName());
        }

    }

    @Override
    public void init_loop(){
        sm.initializeMachine();
        //Replace these preview questions with a JSON file with the pre-sets
        if(!confirmOS){
            telemetry.addData("Please select a file", fList.get(fIndex));

            if(gamepad1.dpad_down && !selOS) {
                fIndex++;
                selOS = true;
            }
            if(gamepad1.dpad_up && !selOS) {
                fIndex--;
                selOS = true;
            }
            if(!gamepad1.dpad_up && !gamepad1.dpad_down) selOS = false;

            fIndex = fIndex > fList.size()-1 ? fList.size()-1 : fIndex < 0 ? 0 : fIndex;

            if(gamepad1.a && !gamepad1.start && !gamepad2.start) confirmOS = true;
        } else {
            File rPath = new File(rFolder, fList.get(fIndex));
            try {
                InputStream fin = new FileInputStream(rPath);
                byte[] raw_inp = new byte[fin.available()];
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
                telemetry.addData("ERROR", e.getCause());
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
        sm.pause(wait);
            if (!loadingSideStart) {
                sm.translate(0, safeSpeed, 5);
            } else {
                if(allianceColor.equals("blue")) {
                    sm.translate(0, safeSpeed, 36.75);
                    sm.translate(-90, safeSpeed, 3.6);
                    sm.rotate(-90, safeSpeed);
                    sm.translate(0, safeSpeed, 7);
                    //Insert Collection System
                    sm.translate(180, safeSpeed, 7);
                    if(returnPath.equals("not_wall")) {
                        sm.translate(-90, safeSpeed, 20);
                    } else {
                        sm.translate(-90, safeSpeed, 40);
                    }
                    sm.translate(180, 1.0, 76);
                    sm.rotate(90, safeSpeed);
                    //sm.translate(0,safeSpeed,4); Add this if our servos cant pick up the foundation at that distance
                    //Insert Foundation Servos
                    sm.translate(180, safeSpeed, 23.1); //This pulls foundation into building site


                    if(returnPath.equals("wall")) {
                        sm.translate(-94,safeSpeed,60);
                    } else {
                        //When Alliance if on the wall
                    }
                    if(sm.next_state_to_execute()) {
                        int frontBlue = r.getColorValue("colorFront", "blue");
                        int backBlue = r.getColorValue("colorBack", "blue");
                        if(frontBlue<11){
                            //WheelSetL is static, might cause problem
                            r.setPower(Robot.wheelSetL[0],.3);
                            r.setPower(Robot.wheelSetL[1],.3);
                        }
                        if(backBlue<11){
                            r.setPower(Robot.wheelSetL[0],-.3);
                            r.setPower(Robot.wheelSetL[1],-.3);
                        }
                        if(frontBlue >= 11 && backBlue >= 11) sm.incrementState();
                    }
                    if(skyStones==2) {
                        sm.translate(0, safeSpeed / 2, 23);
                        sm.translate(90, safeSpeed / 2, 10);
                        sm.translate(-90, safeSpeed / 2, 2);

                        sm.translate(0, safeSpeed / 2, 10);
                        sm.translate(-90, safeSpeed / 2, 3.5);
                        sm.rotate(-90, safeSpeed / 2);
                        sm.translate(0, safeSpeed / 2, 7);
                        //Insert Collection System
                        sm.translate(180, safeSpeed / 2, 7);
                        sm.translate(-90, safeSpeed / 2, 20);
                        sm.translate(180, safeSpeed / 2, 77);
                    }
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
}
