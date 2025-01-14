package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


@TeleOp(name="FTC Starter Kit Example Robot (INTO THE DEEP)", group="Robot")
public class test extends LinearOpMode
{
    @Override
    public void runOpMode()
    {
        telemetry.addLine(String.valueOf(gamepad1.left_stick_x));
        telemetry.update();
    }
}