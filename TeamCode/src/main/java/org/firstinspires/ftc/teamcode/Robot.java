package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareDevice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;

import org.firstinspires.ftc.teamcode.Timer;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;



class Robot{

    private Robot(){

    }

    public Map<String, DcMotor> motors;
    public Map<String, Object> servos;
    public Map<String, Object> sensors;
    List<String> flags = new CopyOnWriteArrayList<>();
    public OpMode opMode = null;
    public static String TAG = "ROBOTCLASS";

    String[][] mtrList = {
            {"mtrFrontLeft","F"},   //Wheel Set 1
            {"mtrBackRight","R"},   //Wheel Set 1
            {"mtrBackLeft","F"},    //Wheel Set 2
            {"mtrFrontRight","R"},  //Wheel Set 2
            {"mtrCollectionLeft","F"},
            {"mtrCollectionRight","R"},
            {"mtrLift","F"}
    };

    String[][] senList = {
            {"color","3c"},
            {"distF","4c"},
            {"distS","5c"}
    };
    String[][] srvList = {
            {"srvClamp","p"},
            {"srvRotator","p"},
            {"srvConveyor","c"}
    };
    static String[] wheelSet1 = {"mtrFrontLeft", "mtrBackRight"};
    static String[] wheelSet2 = {"mtrFrontRight", "mtrBackLeft"};
    static String[] wheelSetL = {"mtrFrontLeft", "mtrBackLeft"};
    static String[] wheelSetR = {"mtrFrontRight", "mtrBackRight"};

    private static Robot currInstance = null;

    public void initialize(OpMode op){
        motors = new HashMap<>();
        servos = new HashMap<>();
        sensors = new HashMap<>();
        flags.clear();

        try {
            for (String[] m : mtrList) {
                DcMotor holder = op.hardwareMap.dcMotor.get(m[0]);
                if (m[1].equals("R")) holder.setDirection(DcMotorSimple.Direction.REVERSE);
                else if (m[1].equals("F")) holder.setDirection(DcMotorSimple.Direction.FORWARD);
                else holder = null;
                if(holder != null) holder.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                motors.put(m[0], holder);
            }
            for (String[] s : srvList) {
                Object holder = null;
                if (s[1].equals("p")) holder = op.hardwareMap.servo.get(s[0]);
                else if (s[1].equals("c")) holder = op.hardwareMap.crservo.get(s[0]);
                servos.put(s[0], holder);
            }
            for(String[] sen : senList){
                    HardwareDevice sensor = op.hardwareMap.get(sen[0]);
                    sensor.resetDeviceConfigurationForOpMode();
                    if (sensor instanceof ColorSensor) {
                        Log.e("ROBOT", "SENSOR IS RIGHT TYPE");
                        sensor = op.hardwareMap.colorSensor.get(sen[0]);
                        ((ColorSensor) sensor).setI2cAddress(I2cAddr.create8bit(Integer.parseInt(sen[1], 16)));
                        ((ColorSensor) sensor).enableLed(true);
                    }
                    sensors.put(sen[0], sensor);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Robot getInstance() {
        currInstance = currInstance == null ? new Robot() : currInstance;
        return currInstance;
    }



    //----ROBOT UTILITY FUNCTIONS----//

    public void setPower(String motorName, double power) {
        if (motors.get(motorName) != null) motors.get(motorName).setPower(power);
    }

    public Integer getEncoderCounts(String motorName) {
        return (motors.get(motorName) != null) ? motors.get(motorName).getCurrentPosition() : null;
    }

    public Double getPower(String motorName) {
        return (motors.get(motorName) != null) ? motors.get(motorName).getPower() : null;
    }

    public void resetEncoder(String...motorNames) {
        for(String name : motorNames) {
            if (motors.get(name) != null) {
                DcMotor.RunMode rm = motors.get(name).getMode();
                setRunMode(name, DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                setRunMode(name, rm);
            } else {
                Log.e(TAG, "resetEncoder: motor is null: " + name);
            }
        }
    }

    public void setRunMode(String motorName, DcMotor.RunMode runMode) {
        if (motors.get(motorName) != null) motors.get(motorName).setMode(runMode);
    }

    public void setTarget(String motorName, int target) {
        if (motors.get(motorName) != null) {
            motors.get(motorName).setTargetPosition(target);
            setRunMode(motorName, DcMotor.RunMode.RUN_TO_POSITION);

        }
    }

    public void initRunToTarget(String motorName, int target, double power) {
        if (motors.get(motorName) != null) {
            setTarget(motorName, target);
            setPower(motorName, power);
        } else {
            Log.e(TAG, "initRunToTarget: failed to run motor: " + motorName + " to target: " + target + " at power: " + power);
        }
    }

    public void initRunToTarget(String motorName, int target, double power, boolean reset) {
        if (motors.get(motorName) != null) {
            if (reset) resetEncoder(motorName);
            initRunToTarget(motorName, target, power);
        }
    }

    public Integer getColorValue(String sensor, String channel) {
        if (sensors.get(sensor) != null && sensors.get(sensor) instanceof ColorSensor) {
            switch (channel) {
                case "red":
                    return ((ColorSensor) sensors.get(sensor)).red();
                case "blue":
                    return ((ColorSensor) sensors.get(sensor)).blue();
                case "green":
                    return ((ColorSensor) sensors.get(sensor)).green();
                case "alpha":
                    return ((ColorSensor) sensors.get(sensor)).alpha();
            }
        }
        return null;
    }

    public Double getDistance(String sensor) {
        if (sensors.get(sensor) != null && sensors.get(sensor) instanceof DistanceSensor) {
            return ((DistanceSensor) sensors.get(sensor)).getDistance(DistanceUnit.CM);
        }
        return null;
    }


    public void resetDriveEncoders() {
        resetEncoder(wheelSet1[0]);
        //resetEncoder(wheelSet1[1]);
        resetEncoder(wheelSet2[0]);
        resetEncoder(wheelSet2[1]);
    }

    public void setDrivePower(double ws1Pow, double ws2Pow) {
        setPower(wheelSet1[0], ws1Pow);
        setPower(wheelSet1[1], ws1Pow);
        setPower(wheelSet2[0], ws2Pow);
        setPower(wheelSet2[1], ws2Pow);
    }

    public void setDriveEncoderTarget(int ws1Target, int ws2Target) {
        setTarget(wheelSet1[0], ws1Target);
        //setTarget(wheelSet1[1], ws1Target);
        setTarget(wheelSet2[0], ws2Target);
        setTarget(wheelSet2[1], ws2Target);
    }

    public void setDriveRunMode(DcMotor.RunMode rm) {
        setRunMode(wheelSet1[0], rm);
        //setRunMode(wheelSet1[1],rm);
        setRunMode(wheelSet2[0], rm);
        setRunMode(wheelSet2[1],rm);
    }

    public void initRunDriveToTarget(int ws1Target, double ws1Pow, int ws2Target, double ws2Pow) {
        setDriveEncoderTarget(ws1Target, ws2Target);
        setDrivePower(ws1Pow, ws2Pow);
        setDriveRunMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void initRunDriveToTarget(int ws1Target, double ws1Pow, int ws2Target, double ws2Pow, boolean reset) {
        if (reset) resetDriveEncoders();
        setDriveEncoderTarget(ws1Target, ws2Target);
        setDrivePower(ws1Pow, ws2Pow);
        setDriveRunMode(DcMotor.RunMode.RUN_TO_POSITION);

    }

    public boolean opModeIsActive() {
        return opMode instanceof LinearOpMode && ((LinearOpMode) opMode).opModeIsActive();
    }

//    public double calculateVelocity(VelocityTask t, long sampleTimeMS) {
//        Timer timer = new Timer();
//        double startVal = t.getSample();
//        while (!timer.hasTimeElapsed(sampleTimeMS)) ;
//        double currentVelocity = (t.getSample() - startVal) / sampleTimeMS * 1000.;
//        Log.d(TAG, "calculateVelocity: current velocity: " + currentVelocity);
//        return currentVelocity;
//    }

    public void setServoPosition(String servoName, double position) {
        Object servo = servos.get(servoName);
        if (servo != null) {
            if (servo instanceof Servo) ((Servo) servo).setPosition(position);
            else if (servo instanceof CRServo) setServoPower(servoName, position);
        } else Log.e(TAG, "setServoPosition: servo is null: " + servoName);
    }

    public void setServoPower( String servoName, double power) {
        Object servo = servos.get(servoName);
        if (servo != null) {
            if (servo instanceof CRServo) ((CRServo) servo).setPower(power);
            else if (servo instanceof Servo) setServoPosition(servoName, power);

        } else Log.e(TAG, "setServoPower: CR servo is null: " + servoName);
    }

    public Boolean hasMotorEncoderReached( String motorName, int encoderCount) {
        return (motors.get(motorName) != null) ? Math.abs(getEncoderCounts(motorName)) >= Math.abs(encoderCount) : null;
    }

    //METHOD TO BE CALLED UPON COMPLETION OF AN AUTONOMOUS PROGRAM IF ENCODERS ARE DESIRED TO BE RESET
    public void finish() {
        Log.i(TAG, "finish: entering finish phase");
        for (String m : motors.keySet()) resetEncoder(m);
    }

}
