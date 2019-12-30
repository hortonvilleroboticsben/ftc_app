package org.firstinspires.ftc.teamcode;

import android.hardware.camera2.CameraAccessException;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;

@TeleOp(name="VisionTest")
public class VisionTest extends OpMode {

    boolean picOS = true;

    @Override
    public void init() {

    }

    @Override
    public void loop() {
        if(gamepad1.a && !gamepad1.start && picOS) {
            try {
                FtcRobotControllerActivity.ftcApp.takePicture();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            picOS = false;
        }
        if(!gamepad1.a) picOS = true;
    }
}
