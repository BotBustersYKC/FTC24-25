package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="Read_pos-debug", group="Robot")
public class debug_2 extends LinearOpMode
{
    public Servo   Servo1       = null; //the wrist servo
    public Servo   Servo2       = null; //folding servo
    public CRServo Servo3 = null;
    public DcMotor extendMotor = null;
    public DcMotor armMotor = null;
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
        Servo1  = hardwareMap.get(Servo.class, "Servo1");
        Servo2 = hardwareMap.get(Servo.class, "Servo2");
        Servo3 = hardwareMap.get(CRServo.class, "Servo3");
        extendMotor = hardwareMap.get(DcMotor.class, "extender");
        armMotor = hardwareMap.get(DcMotor.class, "left_arm");

        Servo2.setDirection(Servo.Direction.REVERSE);

        waitForStart();
        while (opModeIsActive())
        {
            Servo1.setPosition(position);
            Servo2.setPosition(position);

            if (gamepad1.x)
            {
                position += .005;
            }
            if (gamepad1.y)
            {
                position -= .005;
            }


            extendMotor.setTargetPosition(0);
            ((DcMotorEx) extendMotor).setVelocity(2100);
            extendMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            armMotor.setTargetPosition(0);
            ((DcMotorEx) armMotor).setVelocity(2100);
            armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            telemetry.addData("Position:", position);

            telemetry.update();
        }
    }
}

