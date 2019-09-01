package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.Timer;

class StateMachine{

    public String flag = "";
    Robot rbt= Robot.getInstance();
    final String TAG = "StateMachine";
    final double wheelCircummference = 4 * Math.PI;
    final int countsPerRotation = 560;
    int current_number = 0;
    int state_in_progress = 1;
    boolean initOS = true;
    long systemTime;

    @Override
    public String toString(){
        return "cn:"+current_number+"\tSIP:"+state_in_progress+"\tFlag:"+flag;
    }

    boolean next_state_to_execute(){
        return (state_in_progress == ++current_number);
    }
    void initializeMachine(){
        current_number = 0;
    }

    void incrementState(){
        state_in_progress++;
    }

    void reset(){
        state_in_progress = 1;
        current_number = 0;
    }

    boolean waitHasFinished(long milliseconds) {
        boolean returnVal = false;

        if (initOS) {
            systemTime = System.nanoTime() / 1000000;
            initOS = false;
        } else if ((System.nanoTime() / 1000000) - systemTime >= milliseconds) {
            initOS = true;
            returnVal = true;
        }

        return returnVal;
    }

    void SetFlag(StateMachine receiver, String key){
        if(next_state_to_execute()){
            receiver.flag = key;
            incrementState();
        }
    }

    void WaitForFlag(String key){
        if(next_state_to_execute()){
            if(flag.equals(key)){
                incrementState();
            }
        }
    }

    void ClearFlag(){
        if(next_state_to_execute()){
            flag = "";
            incrementState();
        }
    }

/*    public void drive(double distance, double power) {
        double wheelRotations = distance / wheelCircummference;
        int targetEncoderCounts = (int) (wheelRotations * countsPerRotation);
        Log.i(TAG, "drive: Target counts: " + targetEncoderCounts);

        rbt.initRunDriveToTarget(-targetEncoderCounts, power, -targetEncoderCounts, power, true);

        while (rbt.opModeIsActive()) {

            //Log.d(TAG, "turn: current right count: " + mtrRightDrive.getCurrentPosition());
            //Log.d(TAG, "turn: current left count: " + mtrLeftDrive.getCurrentPosition());

            rbt.opMode.telemetry.addData("target", targetEncoderCounts);
            rbt.opMode.telemetry.update();

            if (Math.abs(rbt.getEncoderCounts("mtrLeftDrive")) >= Math.abs(targetEncoderCounts) - 20) {
                rbt.setPower("mtrLeftDrive", 0);
            } else {
                rbt.setPower("mtrLeftDrive", power);
            }

            if (Math.abs(rbt.getEncoderCounts("mtrRightDrive")) >= Math.abs(targetEncoderCounts) - 20) {
                rbt.setPower("mtrRightDrive", 0);
            } else {
                rbt.setPower("mtrRightDrive", power);
            }

            if (rbt.getPower("mtrLeftDrive") == 0 && rbt.getPower("mtrRightDrive") == 0) break;
        }


        rbt.resetDriveEncoders();
        Log.v(TAG, "drive: Successfully drove to target of " + distance + " inches");

    }*/

    //    Mecanum Wheels
    void translate(double degrees, double power, double distance){ // Degrees  0 -> Straight, Degrees 90 -> , Degrees -90 ->
        double wheelRotations = distance / wheelCircummference;
        int targetEncoderCounts = (int) (wheelRotations * countsPerRotation);
        double theta2 = -45 - degrees;
        int wheelSetEncoder1 = (int) (targetEncoderCounts*(Math.cos(theta2) * 180 / Math.PI));
        int wheelSetEncoder2 = (int) (-targetEncoderCounts*(Math.sin(theta2) * 180 / Math.PI));
        double wheelSetPower1 = power*(Math.cos(theta2));
        double wheelSetPower2 = -power*(Math.sin(theta2));




        rbt.initRunDriveToTarget(wheelSetEncoder1, wheelSetPower1, wheelSetEncoder2, wheelSetPower2, true);

        while (rbt.opModeIsActive()) {

            //Log.d(TAG, "turn: current right count: " + mtrRightDrive.getCurrentPosition());
            //Log.d(TAG, "turn: current left count: " + mtrLeftDrive.getCurrentPosition());

            rbt.opMode.telemetry.addData("target", targetEncoderCounts);
            rbt.opMode.telemetry.update();

            if ( (Math.abs(rbt.getEncoderCounts("mtrFrontLeft")) >= Math.abs(wheelSetEncoder1) - 20)
                    && (Math.abs(rbt.getEncoderCounts("mtrBackRight")) >= Math.abs(wheelSetEncoder1) - 20) ) {
                rbt.setPower("mtrFrontLeft", 0);
                rbt.setPower("mtrBackRight",0);
            } else {
                rbt.setPower("mtrFrontLeft", wheelSetPower1);
                rbt.setPower("mtrBackRight", wheelSetPower1);
            }

            if ( (Math.abs(rbt.getEncoderCounts("mtrFrontRight")) >= Math.abs(wheelSetEncoder2) - 20)
                    && (Math.abs(rbt.getEncoderCounts("mtrBackLeft")) >= Math.abs(wheelSetEncoder2) - 20) ) {
                rbt.setPower("mtrFrontRight", 0);
                rbt.setPower("mtrBackLeft",0);
            } else {
                rbt.setPower("mtrFrontRight", wheelSetPower2);
                rbt.setPower("mtrBackLeft", wheelSetPower2);
            }

            if (rbt.getPower("mtrFrontLeft") == 0 && rbt.getPower("mtrBackRight") == 0
                    && rbt.getPower("mtrFrontRight")==0 && rbt.getPower("mtrBackLeft")==0) break;
        }

        rbt.resetDriveEncoders();

    }

    public void turn(double degrees, double power) { //TODO: im dying here
        DcMotor mtrLeftDrive = rbt.motors.get("mtrLeftDrive"), mtrRightDrive = rbt.motors.get("mtrRightDrive");
        double turnCircumference = .6981 * Math.PI;
        double wheelRotations = (turnCircumference / wheelCircummference) * (Math.abs(degrees) / 360);
        int targetEncoderCounts = (int) (wheelRotations * countsPerRotation);
        Log.i(TAG, "turn: Target counts: " + targetEncoderCounts);
        rbt.setDriveRunMode(DcMotor.RunMode.RUN_TO_POSITION);


        if (degrees > 0) {

            rbt.initRunDriveToTarget(targetEncoderCounts, power, -targetEncoderCounts, power, true);

        } else {

            rbt.initRunDriveToTarget(-targetEncoderCounts, power, targetEncoderCounts, power, true);

        }

        while (rbt.opModeIsActive()) {

            Log.d(TAG, "turn: current right count: " + mtrRightDrive.getCurrentPosition());
            Log.d(TAG, "turn: current left count: " + mtrLeftDrive.getCurrentPosition());

            if (Math.abs(rbt.getEncoderCounts("mtrLeftDrive")) >= Math.abs(targetEncoderCounts) - 20) {
                rbt.setPower("mtrLeftDrive", 0);
            } else {
                rbt.setPower("mtrLeftDrive", power);
            }

            if (Math.abs(rbt.getEncoderCounts("mtrRightDrive")) >= Math.abs(targetEncoderCounts) - 20) {
                rbt.setPower("mtrRightDrive", 0);
            } else {
                rbt.setPower("mtrRightDrive", power);
            }

            if (rbt.getPower("mtrLeftDrive") == 0 && rbt.getPower("mtrRightDrive") == 0) break;
        }

        mtrLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        mtrRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Log.v(TAG, "turn: Successfully turned to target of " + degrees + " degrees");
    }

    //    Mecanum Wheels
    void rotate(double degrees, double power){

    }
}
