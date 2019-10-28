package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import static org.firstinspires.ftc.teamcode.Robot.wheelSet1;
import static org.firstinspires.ftc.teamcode.Robot.wheelSet2;

@Autonomous (name = "SpeedTest", group = "Testing")
public class SpeedTest extends OpMode {

    Robot r = null;
    StateMachine s = new StateMachine();

    @Override
    public void init() {
        r = Robot.getInstance();
        r.initialize(this);
    }

    @Override
    public void loop() {
        s.initializeMachine();
        if(s.next_state_to_execute()){
//            r.setDrivePower(0.2,0.2);
            r.setPower(wheelSet1[0],0.8765382124    *.3);
            r.setPower(wheelSet1[1],0.8571775537    *.3);
            r.setPower(wheelSet2[0],0.9452373242    *.3);
            r.setPower(wheelSet2[1],1               *.3);
        }
    }
}
