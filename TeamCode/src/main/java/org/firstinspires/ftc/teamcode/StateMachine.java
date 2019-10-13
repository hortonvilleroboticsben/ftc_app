package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.qualcomm.robotcore.hardware.DcMotor;

import static org.firstinspires.ftc.teamcode.Robot.wheelSet1;
import static org.firstinspires.ftc.teamcode.Robot.wheelSet2;

class StateMachine{

    public String flag = "";
    Robot rbt= Robot.getInstance();
    final String TAG = "StateMachine";
    final double wheelCircumference = 2.7222222222222222222222222222222 * Math.PI;
    final int countsPerRotation = 560;
    int current_number = 0;
    int state_in_progress = 1;
    boolean initOS = true;
    boolean moveInit = true;

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
        double wheelRotations = distance / wheelCircumference;
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
    void translate(double degrees, double power, double distance) { //Degrees0->Straight, Degrees 90 -> Left , Degrees -90 -> Right, Degrees 180 -> Backwards
            //boolean moveInit = true;
        if(next_state_to_execute()) {
            double wheelRotations = distance / wheelCircumference;
            int targetEncoderCounts = (int) (wheelRotations * countsPerRotation);
            double theta2 = (-45 - degrees) / 180.0 * Math.PI;
            int wheelSetEncoder1 = (int) (targetEncoderCounts * (Math.cos(theta2)));
            Log.d("ENCODERS", targetEncoderCounts + "");
            int wheelSetEncoder2 = (int) (-targetEncoderCounts * (Math.sin(theta2)));
            double wheelSetPower1 = power * (Math.cos(theta2));
            double wheelSetPower2 = power * -(Math.sin(theta2));
            //Log.d("ENC_TRANSLATE",String.format("MoveInit:%s",moveInit));
            //if(moveInit) {
            Log.d("ENCODERS", "(" + wheelSetEncoder1 + ":" + wheelSetPower1 + ") , (" + wheelSetEncoder2 + ":" + wheelSetPower2 + ")");

            if (moveInit) {
                rbt.initRunDriveToTarget(wheelSetEncoder1, wheelSetPower1, wheelSetEncoder2, wheelSetPower2*.90, true);
                moveInit = false;
            }

            //  moveInit = false;
            //}

            if (rbt.hasMotorEncoderReached(wheelSet1[1], wheelSetEncoder1)
                    && rbt.hasMotorEncoderReached(rbt.wheelSet2[1], wheelSetEncoder2)) {
                Log.d("ENC_TRANSLATE", String.format("(%d,%d) / (%d,%d)",
                        rbt.getEncoderCounts(wheelSet1[0]),
                        rbt.getEncoderCounts(rbt.wheelSet2[0]),
                        wheelSetEncoder1, wheelSetEncoder2));

                rbt.setDrivePower(-wheelSetPower1,-wheelSetPower2);
                rbt.setPower(wheelSet1[0],0);
                rbt.setPower(wheelSet1[1],0);
                rbt.setPower(wheelSet2[0],0);
                rbt.setPower(wheelSet2[1],0);

                rbt.resetDriveEncoders();
                rbt.setDriveRunMode(DcMotor.RunMode.RUN_USING_ENCODER);
                moveInit = true;
                incrementState();
//            }
            }
        }
    }


    public void rotate(double degrees, double power) {
        if(next_state_to_execute()) {
            double turnCircumference = 14 * Math.PI;
            double wheelRotations = (turnCircumference / wheelCircumference) * (Math.abs(degrees) / 360);
            int targetEncoderCounts = (int) (wheelRotations * countsPerRotation);
            Log.i(TAG, "turn: Target counts: " + targetEncoderCounts);

            rbt.initRunToTarget(rbt.wheelSetL[0], (int) (-Math.signum(degrees) * targetEncoderCounts), power);
            rbt.initRunToTarget(rbt.wheelSetL[1], (int) (-Math.signum(degrees) * targetEncoderCounts), power);
            rbt.initRunToTarget(rbt.wheelSetR[0], (int) (Math.signum(degrees) * targetEncoderCounts), power);
            rbt.initRunToTarget(rbt.wheelSetR[1], (int) (Math.signum(degrees) * targetEncoderCounts), power);


            if (rbt.hasMotorEncoderReached(rbt.wheelSetL[0], targetEncoderCounts) && rbt.hasMotorEncoderReached(rbt.wheelSetR[0], targetEncoderCounts)) {
                rbt.resetDriveEncoders();
                incrementState();
            }
        }

    }

    //    Mecanum Wheels
    //void rotate(double degrees, double power){

   // }
}
