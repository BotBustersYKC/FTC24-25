package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


@TeleOp(name="test", group="Robot")
public class test extends LinearOpMode
{
    @Override
    public void runOpMode()
    {
        waitForStart();
        while (opModeIsActive())
        {
            telemetry.addLine(String.valueOf(gamepad1.left_stick_x));
            telemetry.update();
        }
    }
}