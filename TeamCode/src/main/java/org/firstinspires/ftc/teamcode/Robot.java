package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareDevice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;

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

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;



class Robot extends OpMode {

    private Robot(){

    }

    public static String TAG = "ROBOTCLASS";
    public DcMotor mtrLeftDrive = hardwareMap.dcMotor.get("mtrLeftDrive");
    public DcMotor mtrRightDrive = hardwareMap.dcMotor.get("mtrRightDrive");
    public DcMotor mtrFrontLeft = hardwareMap.dcMotor.get("mtrFrontLeft");
    public DcMotor mtrFrontRight = hardwareMap.dcMotor.get("mtrFrontTight");
    public DcMotor mtrBackLeft = hardwareMap.dcMotor.get("mtrBackLeft");
    public DcMotor mtrBackRight = hardwareMap.dcMotor.get("mtrBackRight");
    public DcMotor mtrArmLift = hardwareMap.dcMotor.get("mtrArmLift");
    public Servo left = hardwareMap.servo.get("srvLeft");
    public Servo right = hardwareMap.servo.get("srvRight");
    private static Robot currInstance = null;

    public static Robot getInstance() {
        currInstance = currInstance == null ? new Robot() : currInstance;
        return currInstance;
    }

    public Map<String, DcMotor> motors;
    public Map<String, HardwareDevice> servos;
    public Map<String, HardwareDevice> sensors;
    List<String> flags = new CopyOnWriteArrayList<>();
    public OpMode opMode = null;



    @Override
    public void init() {

    }

    @Override
    public void loop() {

    }

    public void initialize(OpMode opMode) {

        motors = new HashMap<>();
        servos = new HashMap<>();
        sensors = new HashMap<>();
        flags.clear();
        telemetry = opMode.telemetry;

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

    public void resetEncoder(String motorName) {
        if (motors.get(motorName) != null) {
            setRunMode(motorName, DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            setRunMode(motorName, DcMotor.RunMode.RUN_USING_ENCODER);
        } else {
            Log.e(TAG, "resetEncoder: motor is null: " + motorName);
        }
    }

    public void setRunMode(String motorName, DcMotor.RunMode runMode) {
        if (motors.get(motorName) != null) motors.get(motorName).setMode(runMode);
    }

    public void setTarget(String motorName, int target) {
        if (motors.get(motorName) != null) {
            setRunMode(motorName, DcMotor.RunMode.RUN_TO_POSITION);
            motors.get(motorName).setTargetPosition(target);
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

    public void resetDriveEncoders() {
        resetEncoder("mtrLeftDrive");
        resetEncoder("mtrRightDrive");
    }

    public void setDrivePower(double lPow, double rPow) {
        setPower("mtrLeftDrive", lPow);
        setPower("mtrRightDrive", rPow);
    }

    public void setDriveEncoderTarget(int lTarget, int rTarget) {
        setTarget("mtrLeftDrive", lTarget);
        setTarget("mtrRightDrive", rTarget);
    }

    public void setDriveRunMode(DcMotor.RunMode rm) {
        setRunMode("mtrLeftDrive", rm);
        setRunMode("mtrRightDrive", rm);
    }

    public void initRunDriveToTarget(int lTarget, double lPow, int rTarget, double rPow) {
        initRunToTarget("mtrLeftDrive", lTarget, lPow);
        initRunToTarget("mtrRightDrive", rTarget, rPow);
    }

    public void initRunDriveToTarget(int lTarget, double lPow, int rTarget, double rPow, boolean reset) {
        if (reset) resetDriveEncoders();
        setDriveRunMode(DcMotor.RunMode.RUN_TO_POSITION);
        setDriveEncoderTarget(lTarget, rTarget);
        setDrivePower(lPow, rPow);
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
        HardwareDevice servo = servos.get(servoName);
        if (servo != null) {
            if (servo instanceof Servo) ((Servo) servo).setPosition(position);
            else if (servo instanceof CRServo) setServoPower(servoName, position);
        } else Log.e(TAG, "setServoPosition: servo is null: " + servoName);
    }

    public void setServoPower( String servoName, double power) {
        HardwareDevice servo = servos.get(servoName);
        if (servo != null) {
            if (servo instanceof CRServo) ((CRServo) servo).setPower(power);
            else if (servo instanceof Servo) setServoPosition(servoName, power);

        } else Log.e(TAG, "setServoPower: CR servo is null: " + servoName);
    }

    public Boolean hasMotorEncoderReached( String motorName, int encoderCount) {
        return (motors.get(motorName) != null) ? Math.abs(getEncoderCounts(motorName)) >= Math.abs(encoderCount) : null;
    }

    //----ROBOT FUNCTIONS BEGIN----//
    //----ROBOT FUNCTIONS BEGIN----//

    public void pause(long msec) {
        Timer t = new Timer();
       // while (opModeIsActive() && !t.hasTimeElapsed(msec)) ;
    }

//    Mecanum Wheels
    public void rotate(double degrees, double power){

    }

//    Mecanum Wheels
    public void translate(double degrees, double power){
        double theta2 = 45 - degrees;
        double wheelSetPower2 = power*(Math.sin(theta2));
        double wheelSetPower1 = power*(Math.cos(theta2));

        //set 1 & 4 for wheelSetOne
        mtrFrontLeft.setPower(wheelSetPower1);
        mtrBackRight.setPower(wheelSetPower1);
        //set 2 & 3 for wheelSetTwo
        mtrFrontRight.setPower(wheelSetPower2);
        mtrBackLeft.setPower(wheelSetPower2);


    }



    //DRIVE WITH DEFAULT POWER OF 72%
//    public void drive(double distance) {
//        drive(distance, 0.72);
//    }

    //DRIVE WITH SPECIFIED POWER
//    public void drive(double distance, double power) {
//        DcMotor mtrLeftDrive = motors.get("mtrLeftDrive"), mtrRightDrive = motors.get("mtrRightDrive");
//        double wheelRotations = distance / config.getWheelCircumference();
//        int targetEncoderCounts = (int) (wheelRotations * config.getCountsPerRotation());
//        Log.i(TAG, "drive: Target counts: " + targetEncoderCounts);
//
//        initRunDriveToTarget(-targetEncoderCounts, power, -targetEncoderCounts, power, true);
//
//        while (opModeIsActive()) {
//
//            Log.d(TAG, "turn: current right count: " + mtrRightDrive.getCurrentPosition());
//            Log.d(TAG, "turn: current left count: " + mtrLeftDrive.getCurrentPosition());
//
//            opMode.telemetry.addData("target", targetEncoderCounts);
//            opMode.telemetry.update();
//
//            if (Math.abs(getEncoderCounts("mtrLeftDrive")) >= Math.abs(targetEncoderCounts) - 20) {
//                setPower("mtrLeftDrive", 0);
//            } else {
//                setPower("mtrLeftDrive", power);
//            }
//
//            if (Math.abs(getEncoderCounts("mtrRightDrive")) >= Math.abs(targetEncoderCounts) - 20) {
//                setPower("mtrRightDrive", 0);
//            } else {
//                setPower("mtrRightDrive", power);
//            }
//
//            if (getPower("mtrLeftDrive") == 0 && getPower("mtrRightDrive") == 0) break;
//        }
//
//
//        resetDriveEncoders();
//        Log.v(TAG, "drive: Successfully drove to target of " + distance + " inches");
//
//    }


    //TURN WITH DEFAULT POWER OF 40%
//    public void turn(double degrees) {
//        turn(degrees, 0.4);
//    }

    //TURN WITH SPECIFIED POWER
//    public void turn(double degrees, double power) {
//        DcMotor mtrLeftDrive = motors.get("mtrLeftDrive"), mtrRightDrive = motors.get("mtrRightDrive");
//        double turnCircumference = config.getTurnDiameter() * Math.PI;
//        double wheelRotations = (turnCircumference / config.getWheelCircumference()) * (Math.abs(degrees) / 360);
//        int targetEncoderCounts = (int) (wheelRotations * config.getCountsPerRotation());
//        Log.i(TAG, "turn: Target counts: " + targetEncoderCounts);
//        setDriveRunMode(DcMotor.RunMode.RUN_TO_POSITION);
//
//
//        if (degrees > 0) {
//
//            initRunDriveToTarget(targetEncoderCounts, power, -targetEncoderCounts, power, true);
//
//        } else {
//
//            initRunDriveToTarget(-targetEncoderCounts, power, targetEncoderCounts, power, true);
//
//        }
//
//        while (opModeIsActive()) {
//
//            Log.d(TAG, "turn: current right count: " + mtrRightDrive.getCurrentPosition());
//            Log.d(TAG, "turn: current left count: " + mtrLeftDrive.getCurrentPosition());
//
//            if (Math.abs(getEncoderCounts("mtrLeftDrive")) >= Math.abs(targetEncoderCounts) - 20) {
//                setPower("mtrLeftDrive", 0);
//            } else {
//                setPower("mtrLeftDrive", power);
//            }
//
//            if (Math.abs(getEncoderCounts("mtrRightDrive")) >= Math.abs(targetEncoderCounts) - 20) {
//                setPower("mtrRightDrive", 0);
//            } else {
//                setPower("mtrRightDrive", power);
//            }
//
//            if (getPower("mtrLeftDrive") == 0 && getPower("mtrRightDrive") == 0) break;
//        }
//
//        mtrLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        mtrRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        Log.v(TAG, "turn: Successfully turned to target of " + degrees + " degrees");
//    }

    /* DEGREES CONTROLS THE DISTANCE OF THE TURN
     * THE SIGN OF DEGREES CONTROLS WHICH MOTOR
     * THE SIGN OF POWER CONTROLS THE DIRECTION OF THE MOTOR */
//    public void owTurn(double degrees, double power) {
//        double turnCircumference = 2 * config.getTurnDiameter() * Math.PI;
//        double wheelRotations = (turnCircumference / config.getWheelCircumference()) * (Math.abs(degrees) / 360);
//        int targetEncoderCounts = (int) (wheelRotations * config.getCountsPerRotation() * Math.signum(power));
//
//        Log.i(TAG, "owturn: Target counts: " + targetEncoderCounts);
//
//        //TODO TEST REMOVING THE FOLLOWING LINE OF CODE BECAUSE IT IS UNNECCESSARY AND POSSIBLY GIVING ERRORS
////        setDriveRunMode(DcMotor.RunMode.RUN_TO_POSITION);
//
//        boolean targetReached = false;
//
//        if (degrees > 0) {
//
//            if (power < 0) {
//                Log.i(TAG, "owturn: power less than 0");
//            }
//
//            initRunDriveToTarget(0, 0, targetEncoderCounts, power, true);
//
//            while (opModeIsActive() && Math.abs(getEncoderCounts("mtrRightDrive")) < Math.abs(targetEncoderCounts) - 20) {
//                Log.d(TAG, "owturn: current right count: " + getEncoderCounts("mtrRightDrive"));
//                Log.d(TAG, "owturn: current right power: " + getPower("mtrRightDrive"));
//            }
//            setDrivePower(0.0, 0.0);
//
//        } else {
//
//            if (power < 0) {
//                Log.i(TAG, "owturn: power less than 0");
//            }
//
//            initRunDriveToTarget(targetEncoderCounts, power, 0, 0, true);
//
//            while (opModeIsActive() && Math.abs(getEncoderCounts("mtrLeftDrive")) < Math.abs(targetEncoderCounts) - 20) {
//                Log.d(TAG, "owturn: current left count: " + getEncoderCounts("mtrLeftDrive"));
//                Log.d(TAG, "owturn: current left power: " + getPower("mtrLeftDrive"));
//            }
//            setDrivePower(0.0, 0.0);
//
//        }
//    }

    //METHOD TO BE CALLED UPON COMPLETION OF AN AUTONOMOUS PROGRAM IF ENCODERS ARE DESIRED TO BE RESET
    public void finish() {
        Log.i(TAG, "finish: entering finish phase");
        for (String m : motors.keySet()) resetEncoder(m);
    }

}
