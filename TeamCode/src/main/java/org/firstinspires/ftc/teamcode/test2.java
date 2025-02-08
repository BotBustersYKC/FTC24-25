package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;


public class test2 extends OpMode{
    public CRServo intake;
    @Override
    public void init() {
        intake = hardwareMap.get(CRServo.class, "intake");
    }

    @Override
    public void loop() {
        if (gamepad1.a){
            intake.setPower(1);
        }
        if (gamepad1.b){
            intake.setPower(-1);
        }
        intake.setPower(0);
    }
}

