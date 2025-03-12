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
    public  Servo Servo3 = null;
    public CRServo Servo4 = null;
    public DcMotor extendMotor = null;
    public DcMotor armMotor = null;
    double Servo1_pos;
    double Servo2_pos;
    double position;
    double servo_3_pos;
    double extend;
    double rotate;

    final double ARM_TICKS_PER_DEGREE =
            28 // number of encoder ticks per rotation of the bare motor
                    * 250047.0 / 4913.0 // This is the exact gear ratio of the 50.9:1 Yellow Jacket gearbox
                    * 100.0 / 20.0 // This is the external gear reduction, a 20T pinion gear that drives a 100T hub-mount gear
                    * 1/360.0; // we want ticks per degree, not per rotation
    final double EXTEND_TICKS_PER_DEGREE =
            28 //encoder ticks
                    *250047.0/4913.0 // exact ratio
                    *1/360.0; // ticks per degree

    @Override

    public void runOpMode()
    {
        Servo1  = hardwareMap.get(Servo.class, "Servo1");
        Servo2 = hardwareMap.get(Servo.class, "Servo2");
        Servo3 = hardwareMap.get(Servo.class, "Servo3");
        Servo4 = hardwareMap.get(CRServo.class, "Servo4");
        extendMotor = hardwareMap.get(DcMotor.class, "extender");
        armMotor = hardwareMap.get(DcMotor.class, "left_arm");

        Servo2.setDirection(Servo.Direction.REVERSE);
        Servo3.setDirection(Servo.Direction.REVERSE);

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


            Servo3.setPosition(servo_3_pos);
            if (gamepad1.dpad_up)
            {
                servo_3_pos += .005;
            }
            if (gamepad1.dpad_down)
            {
                servo_3_pos -= .005;
            }

            if (gamepad1.dpad_left)
            {
                Servo4.setPower(1);
            }
            if (gamepad1.dpad_right)
            {
                Servo4.setPower(0);
            }
            if (gamepad1.left_bumper)
            {
                extend += 5*EXTEND_TICKS_PER_DEGREE;
            }
            if (gamepad1.right_bumper)
            {
                extend -= 5*EXTEND_TICKS_PER_DEGREE;
            }

            if (gamepad1.a)
            {
                rotate += .3*ARM_TICKS_PER_DEGREE;
            }
            if (gamepad1.b)
            {
                rotate -= .3*ARM_TICKS_PER_DEGREE;
            }


            extendMotor.setTargetPosition((int) extend);
            ((DcMotorEx) extendMotor).setVelocity(2100);
            extendMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            armMotor.setTargetPosition((int) rotate);
            ((DcMotorEx) armMotor).setVelocity(2100);
            armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            telemetry.addData("Position:", position);
            telemetry.addData("Rotating Servo:", servo_3_pos);
            telemetry.addData("Extender position", extend);
            telemetry.addData("Arm position", rotate/ARM_TICKS_PER_DEGREE);
            telemetry.update();
        }
    }
}

