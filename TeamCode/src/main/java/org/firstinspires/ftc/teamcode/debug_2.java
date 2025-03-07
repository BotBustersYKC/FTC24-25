package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="Readposdebug", group="Robot")
public class debug_2 extends LinearOpMode
{
    public DcMotor extendMotor  = null;
    public Servo   Servo1       = null; //the wrist servo
    public Servo   Servo2       = null; //folding servo
    double Servo1_pos;
    double Servo2_pos;
    double extend_pos = 0;

    final double EXTEND_TICKS_PER_DEGREE =
            28 //encoder ticks
                    *250047.0/4913.0 // exact ratio
                    *1/360.0; // ticks per degree

    @Override

    public void runOpMode()
    {
        Servo1  = hardwareMap.get(Servo.class, "Servo1"); //
        Servo2 = hardwareMap.get(Servo.class, "Servo2"); //
        extendMotor = hardwareMap.get(DcMotor.class, "extender");

        extendMotor.setTargetPosition(0);
        extendMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        extendMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        waitForStart();
        while (opModeIsActive())
        {

            Servo1_pos = Servo1.getPosition();
            Servo2_pos = Servo2.getPosition();

            if (gamepad1.a)
            {
                extend_pos = 2000*EXTEND_TICKS_PER_DEGREE;
            }
            if (gamepad1.b)
            {
                extend_pos = 0;
            }

            telemetry.addData("Servo1 pos", Servo1_pos);
            telemetry.addData("Servo2 pos", Servo2_pos);

            telemetry.update();
        }
    }
}

