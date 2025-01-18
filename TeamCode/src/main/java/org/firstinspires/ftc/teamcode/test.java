package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp(name="test", group="Robot")
public class test extends LinearOpMode
{
    public DcMotor leftDriveF  = null; //the left drivetrain motor
    public DcMotor rightDriveF = null; //the right drivetrain motor
    public DcMotor leftDriveR  = null; //the left drivetrain motor
    public DcMotor rightDriveR = null; //the right drivetrain motor
    public DcMotor armMotor    = null; //the arm motor
    public DcMotor extendMotor = null; // extender motor
    public CRServo intake      = null; //the active intake servo
    public Servo   wrist       = null; //the wrist servo
    public Servo   folding     = null; //folding servo

    double leftF = 0;
    double rightF = 0;
    double leftR = 0;
    double rightR = 0;
    double arm = 0;
    double extend = 0;
    double intake_pow = 0;
    double wrist_pow = 0;
    double fold = 0;

    @Override

    public void runOpMode()
    {
        leftDriveF = hardwareMap.get(DcMotor .class, "left_front_drive"); //the left drivetrain motor //2C
        leftDriveR = hardwareMap.get(DcMotor.class, "left_rear_drive");//1C
        rightDriveF = hardwareMap.get(DcMotor.class, "right_front_drive");//the right drivetrain motor //3C
        rightDriveR = hardwareMap.get(DcMotor.class, "right_rear_drive");//0C
        armMotor   = hardwareMap.get(DcMotor.class, "left_arm"); //the arm motor  //1E
        extendMotor = hardwareMap.get(DcMotor.class, "extender"); // extender motor //0E
        intake = hardwareMap.get(CRServo .class, "intake"); //0E
        wrist  = hardwareMap.get(Servo .class, "wrist"); //1E
        folding = hardwareMap.get(Servo.class, "folding"); //2E

        waitForStart();
        while (opModeIsActive())
        {
            leftDriveF.setPower(gamepad1.left_stick_x*leftF);
            leftDriveR.setPower(gamepad1.left_stick_x*leftR);
            rightDriveF.setPower(gamepad1.left_stick_x*rightF);
            rightDriveR.setPower(gamepad1.left_stick_x*rightR);
            armMotor.setPower(gamepad1.left_stick_x*arm);
            extendMotor.setPower(gamepad1.left_stick_x*extend);
            intake.setPower(gamepad1.left_stick_x*intake_pow);
            wrist.setPosition(gamepad1.left_stick_x*wrist_pow);
            folding.setPosition(gamepad1.left_stick_x*fold);

            if (gamepad1.right_bumper)
            {
                leftR = 0;
                leftF = 0;
                rightF = 0;
                rightR = 0;
                arm = 0;
                extend = 0;
                intake_pow = 0;
                wrist_pow = 0;
                fold = 0;
            }
            else if (gamepad1.a)
            {
                leftF = .1;
            }
            else if(gamepad1.b)
            {
                rightF = .1;
            }
            else if (gamepad1.x)
            {
                leftR = .1;
            }
            else if (gamepad1.y)
            {
                rightR = .1;
            }
            else  if (gamepad1.dpad_up)
            {
                arm = .1;
            }
            else if (gamepad1.dpad_right)
            {
                extend = .1;
            }
            else if (gamepad1.dpad_down)
            {
                intake_pow = .1;
            }
            else if (gamepad1.dpad_left)
            {
                wrist_pow = .1;
            }
            else if (gamepad1.left_bumper)
            {
                fold = .1;
            }

            telemetry.addLine(String.valueOf(gamepad1.left_stick_x));
            telemetry.update();
        }
    }
}

