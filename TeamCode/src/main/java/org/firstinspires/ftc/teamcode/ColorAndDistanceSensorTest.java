package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;


@TeleOp(name = "Color & Distance Sensor Test", group ="test")
public class ColorAndDistanceSensorTest extends OpMode {

    Robot r;
    ColorSensor color;
    Integer red, blue, green;

    @Override
    public void init() {
        r = Robot.getInstance();
        color = hardwareMap.colorSensor.get("color");
    }

    @Override
    public void loop() {

        red = r.getColorValue("color", "red");
        blue = r.getColorValue("color", "blue");
        green = r.getColorValue("color", "green");


        telemetry.addData("RED", red+"");
        telemetry.addData("BLUE", blue+"");
        telemetry.addData("GREEN", green+"");
    }
}
