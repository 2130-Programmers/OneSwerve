// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxAlternateEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class SwerveMotor extends SubsystemBase {
  /** Creates a new SwerveMotor. */


  //creating our motors and encoder
  public CANSparkMax driveMotor; //controls the drive motor
  public CANSparkMax rotationMotor; //controls the rotation of the unit
  public CANSparkMax encoderMotor; // for encoder

  //creating variables
  
  private static RelativeEncoder rotationEncoder;

  public double encoderRemainingValue;
  public double pointSet = 0;
  public double reverse = 0;
  
  //We need to find a good way to make this worj with SparkMaxs
  /**sets min and max output
   * 
   * @param peak - (double) The peak output for the motors, counts as forward or reverse. In amps
   * @return void
   */
   private void setMinMaxOutput(int peak){
     rotationMotor.setSmartCurrentLimit(peak);
   }

   //what is done here with the @param is we are adding a comment to our method so that when you call to it it will say
   //what each parameter will be, you can also say @return ("return type") to say which return type it will be

   /**Class for an entire swerve unit
    * 
    * @param motorDeviceNumber - (int) CAN Device ID of the Rotation Spark
    * @param driveMotorNumber - (int) Can device ID of the drive Spark
    * @param encoderMotorNumber - (int) can device id of the encoder motor
    */
  public SwerveMotor(int rotate, int drive, int encoder) {
    //when calling swerve motor we put in parameters which will assign these motors and encoders numbers and ports
    rotationMotor = new CANSparkMax(rotate, MotorType.kBrushless);
    driveMotor = new CANSparkMax(drive, MotorType.kBrushless);
    encoderMotor = new CANSparkMax(encoder, MotorType.kBrushed);

    rotationEncoder = encoderMotor.getAlternateEncoder(SparkMaxAlternateEncoder.Type.kQuadrature, 4069);
    //this is where I would use SetMinMaxOutput
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  //gets the value of an encoder from 0 to 420, we transfer negetive numbers to their positive counterpart
  public double currentEncoderCount(){
    if(rotationEncoder.getPosition() >= 0){
      return rotationEncoder.getPosition();
    }else{
      return rotationEncoder.getPosition() + 420;
    }
  }

  /**sets the speed for the rotation motor
   * 
   * @param speed - (double) the desired power in percent [-1,1]
   * @return (void)
   */
  private void moveMotor(double speed){
    rotationMotor.set(speed);
  }

  //stops the rotation motor
  private void stopMotors(){
    rotationMotor.set(0);
  }

  /**
   * Takes a joystick input and turns the rotation motor to the equivilant of the position in ticks
   * 
   * @param targetX - (doubles) The joystick's current X-value
   * @param targetY - (doubles) The joystick's currentmY-Value
   * 
   * @return (void)
   */
  public void pointToTarget(double target){
    //some local variables to use in calculations
    double flip = 0;
    double currentposition = currentEncoderCount();
    double desiredTarget = target;
    encoderRemainingValue = desiredTarget - (currentposition+=flip);
    double directionMultiplier = 0;

    
    if(encoderRemainingValue>420){
      encoderRemainingValue -= 420;
    }else if(encoderRemainingValue<-420){
      encoderRemainingValue += 420;
    }

    //preliminarily checking to see if it is at the value
    if((encoderRemainingValue != 0) || (encoderRemainingValue - 420 != 0) || (encoderRemainingValue +420 != 0)){

      //checks to see the direction needed to go, basically the formula a/|a|
      // if we need to go to a positive direction a/|a| = 1
      // if we need to go negetive -a/|-a| = -1
      if(encoderRemainingValue>210){
        directionMultiplier = Math.round((encoderRemainingValue-420)/Math.abs(encoderRemainingValue-420));
      }else if(encoderRemainingValue<210){
        directionMultiplier = Math.round((encoderRemainingValue+420)/Math.abs(encoderRemainingValue+420));
      }else if(encoderRemainingValue < 210 && encoderRemainingValue > -210 && encoderRemainingValue != 0){
        directionMultiplier = Math.round((encoderRemainingValue)/Math.abs(encoderRemainingValue));
      }else{
        directionMultiplier = 1;
      }

      if(Math.abs(encoderRemainingValue)>105){
        if(flip > 0){
          flip-=210;
        }else{
          flip+=210;
        }
      }

      //goes towards the point, if it is outside the large error it goes fast, if it is
      //in that range it goes at the slow speed untill smaller than the small error
      if(Math.abs(encoderRemainingValue) > Constants.LargeSwerveRotationError){
        moveMotor(Constants.FastSwerveRotationSpeed*-directionMultiplier);
      }else if(Math.abs(encoderRemainingValue) > Constants.SmallSwerveRotationError){
        moveMotor(Constants.SlowSwerveRotationSpeed*-directionMultiplier);
      }else{
        stopMotors();
      }
    }
  }

  //sets the encoder to zero
  public void zeroEncoder(){
    rotationEncoder.setPosition(0);
  }

  //gets the encoder value but keeps it on a range from -420 to 420
  public double encoderValue(){
    double encoder = rotationEncoder.getPosition();

    if(encoder>420){
      encoder -= 420;
    }else if(encoder<-420){
      encoder += 420;
    }

    return encoder;
  }

  //points the swerve to zero
  public void goToZero(){
    pointToTarget(0);
  }


  //function to make the unite move
  public void drive(double speed, double angle){
    double revamp = 0;
    encoderValue();

    if(1 < Math.abs(speed)){
      revamp = (speed/Math.abs(speed));
    }else{
      revamp = (speed);
    }

    driveMotor.set(revamp);

    if(angle<0){
      /**I don't understand why I did this, *REWORK*
       * |-7+1|+1 = 7 but it would be the same without the +1's
       */
      pointSet = (Math.abs(1+angle)+1)*210;
    }else{
      pointSet = angle*210;
    }

    pointToTarget(pointSet);
  }

  public void staticAngle(double speed, double angle){
    pointToTarget(angle);
    driveMotor.set(speed);
  }
}