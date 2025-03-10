package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="Read_pos-debug", group="Robot")
public class debug_2 extends LinearOpMode
{
    public Servo   Servo1       = null; //the wrist servo
    public Servo   Servo2       = null; //folding servo
    public DcMotor extendMotor = null;
    double Servo1_pos;
    double Servo2_pos;
    double position;
    double extend;
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

        Servo2.setDirection(Servo.Direction.REVERSE);

        waitForStart();
        while (opModeIsActive())
        {
            Servo1.setPosition(0);
            Servo2.setPosition(0);

            if (gamepad1.a)
            {
                extend = 2000*EXTEND_TICKS_PER_DEGREE;
            }
            if (gamepad1.b)
            {
                extend = 0;
            }

            extendMotor.setTargetPosition((int) (extend));
            ((DcMotorEx) extendMotor).setVelocity(2100);
            extendMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            telemetry.addData("Servo1 pos", Servo1_pos);
            telemetry.addData("Servo2 pos", Servo2_pos);

            telemetry.update();
        }
    }
}

