package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="Read_pos-debug", group="Robot")
public class debug_2 extends LinearOpMode
{
    public Servo   wrist       = null; //the wrist servo
    public Servo   folding     = null; //folding servo
    public Servo   rotate       = null; // rotating servo arm

    double wrist_pos = 0;
    double fold = 0;
    double rotate_pos = 0;



    @Override

    public void runOpMode()
    {
        wrist  = hardwareMap.get(Servo.class, "wrist"); //
        folding = hardwareMap.get(Servo.class, "folding"); //
        rotate = hardwareMap.get(Servo.class, "rotate"); //

        waitForStart();
        while (opModeIsActive())
        {

            wrist_pos = wrist.getPosition();
            fold = folding.getPosition();
            rotate_pos = rotate.getPosition();

            telemetry.addData("Wrist Power", wrist_pos);
            telemetry.addData("Fold position", fold);
            telemetry.addData("Rotate position", rotate_pos);

            telemetry.update();
        }
    }
}

