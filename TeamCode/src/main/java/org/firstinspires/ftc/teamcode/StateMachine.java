package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.qualcomm.robotcore.hardware.DcMotor;

class StateMachine{

    public String flag = "";
    Robot rbt= Robot.getInstance();
    final String TAG = "StateMachine";
    final double wheelCircumference = 4 * Math.PI;
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

    //    Mecanum Wheels
    void translate(double degrees, double power, double distance){ //Degrees0->Straight, Degrees 90 -> Left , Degrees -90 -> Right, Degrees 180 -> Backwards
        double wheelRotations = distance / wheelCircumference;
        int targetEncoderCounts = (int) (wheelRotations * countsPerRotation);
        double theta2 = -45 - degrees;
        int wheelSetEncoder1 = (int) (targetEncoderCounts*(Math.cos(theta2) * 180 / Math.PI));
        int wheelSetEncoder2 = (int) (-targetEncoderCounts*(Math.sin(theta2) * 180 / Math.PI));
        double wheelSetPower1 = power*(Math.cos(theta2));
        double wheelSetPower2 = power*-(Math.sin(theta2));

        rbt.initRunDriveToTarget(wheelSetEncoder1, wheelSetPower1, wheelSetEncoder2, wheelSetPower2, true);

        while (rbt.opModeIsActive()) {

            //Log.d(TAG, "turn: current right count: " + mtrRightDrive.getCurrentPosition());
            //Log.d(TAG, "turn: current left count: " + mtrLeftDrive.getCurrentPosition());

            rbt.opMode.telemetry.addData("target", targetEncoderCounts);
            rbt.opMode.telemetry.update();

            if ( (Math.abs(rbt.getEncoderCounts(rbt.wheelSet1[0])) >= Math.abs(wheelSetEncoder1) - 20)
                    && (Math.abs(rbt.getEncoderCounts(rbt.wheelSet1[1])) >= Math.abs(wheelSetEncoder1) - 20) ) {
                rbt.setPower(rbt.wheelSet1[0], 0);
                rbt.setPower(rbt.wheelSet1[1],0);
            } else {
                rbt.setPower(rbt.wheelSet1[0], wheelSetPower1);
                rbt.setPower(rbt.wheelSet1[1], wheelSetPower1);
            }

            if ( (Math.abs(rbt.getEncoderCounts(rbt.wheelSet2[0])) >= Math.abs(wheelSetEncoder2) - 20)
                    && (Math.abs(rbt.getEncoderCounts(rbt.wheelSet2[1])) >= Math.abs(wheelSetEncoder2) - 20) ) {
                rbt.setPower(rbt.wheelSet2[0], 0);
                rbt.setPower(rbt.wheelSet2[1],0);
            } else {
                rbt.setPower(rbt.wheelSet2[0], wheelSetPower2);
                rbt.setPower(rbt.wheelSet2[1], wheelSetPower2);
            }

            if (rbt.getPower(rbt.wheelSet1[0]) == 0 && rbt.getPower(rbt.wheelSet1[1]) == 0
                    && rbt.getPower(rbt.wheelSet2[1])==0 && rbt.getPower(rbt.wheelSet2[1])==0) break;
        }

        rbt.resetDriveEncoders();

    }

    public void rotate(double degrees, double power) {
        double turnCircumference = 14 * Math.PI;
        double wheelRotations = (turnCircumference / wheelCircumference) * (Math.abs(degrees) / 360);
        int targetEncoderCounts = (int) (wheelRotations * countsPerRotation);
        Log.i(TAG, "turn: Target counts: " + targetEncoderCounts);
        rbt.setDriveRunMode(DcMotor.RunMode.RUN_TO_POSITION);

        if (degrees > 0) {

            rbt.initRunDriveToTarget(targetEncoderCounts, power, -targetEncoderCounts, power, true);

        } else {

            rbt.initRunDriveToTarget(-targetEncoderCounts, power, targetEncoderCounts, power, true);

        }

        while (rbt.opModeIsActive()) {

            //Log.d(TAG, "turn: current right count: " + mtrRightDrive.getCurrentPosition());
            //Log.d(TAG, "turn: current left count: " + mtrLeftDrive.getCurrentPosition());

            if ( (Math.abs(rbt.getEncoderCounts(rbt.wheelSetL[0])) >= Math.abs(targetEncoderCounts) - 20)
                    && (Math.abs(rbt.getEncoderCounts(rbt.wheelSetL[1])) >= Math.abs(targetEncoderCounts) - 20) ) {
                rbt.setPower(rbt.wheelSetL[0], 0);
                rbt.setPower(rbt.wheelSetL[1],0);
            } else {
                rbt.setPower(rbt.wheelSetL[0], power);
                rbt.setPower(rbt.wheelSetL[1], power);
            }

            if ( (Math.abs(rbt.getEncoderCounts(rbt.wheelSetR[0])) >= Math.abs(targetEncoderCounts) - 20)
                    && (Math.abs(rbt.getEncoderCounts(rbt.wheelSetR[1])) >= Math.abs(targetEncoderCounts) - 20) ) {
                rbt.setPower(rbt.wheelSetR[0], 0);
                rbt.setPower(rbt.wheelSetR[1],0);
            } else {
                rbt.setPower(rbt.wheelSetR[0], power);
                rbt.setPower(rbt.wheelSetR[1], power);
            }

            if (rbt.getPower(rbt.wheelSetL[0]) == 0 && rbt.getPower(rbt.wheelSetL[1]) == 0
                    && rbt.getPower(rbt.wheelSetR[0])==0 && rbt.getPower(rbt.wheelSetR[1])==0) break;
        }

        rbt.setDriveRunMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Log.v(TAG, "turn: Successfully turned to target of " + degrees + " degrees");
    }

    //    Mecanum Wheels
    //void rotate(double degrees, double power){

   // }
}
