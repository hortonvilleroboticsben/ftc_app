package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;

import org.firstinspires.ftc.robotcore.internal.usb.exception.RobotUsbTimeoutException;


@TeleOp(name = "Color & Distance Sensor Test", group ="test")
public class ColorAndDistanceSensorTest extends OpMode {

    Robot r = Robot.getInstance();

    @Override
    public void init() {
        r.initialize(this);
    }

    @Override
    public void loop() {

        telemetry.addData("RED", r.getColorValue("color","red"));
        telemetry.addData("BLUE", r.getColorValue("color","blue"));
        telemetry.addData("GREEN", r.getColorValue("color","green"));
    }
}
