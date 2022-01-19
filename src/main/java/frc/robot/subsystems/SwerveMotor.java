// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;


import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class SwerveMotor extends SubsystemBase {
  /** Creates a new SwerveMotor. */


  //creating our motors and encoder
  public CANSparkMax driveMotor; //controls the drive motor
  public CANSparkMax rotationMotor; //controls the rotation of the unit
  public static TalonSRX encoderMotor; // for encoder

  //creating variables
  
  public static double rotationEncoder;

  public double encoderRemainingValue;
  public double pointSet = 0;
  public double reverse = 0;
  public double encoderVal = 0;
  public double ticksInRotation = Constants.EncoderTicksInQuadrant;
  
  //We need to find a good way to make this worj with SparkMaxs
  /**sets min and max output
   * 
   * @param peak - (double) The peak output for the motors, counts as forward or reverse. In amps
   * @return void
   */

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
    encoderMotor = new TalonSRX(encoder);

    rotationEncoder = (encoderMotor.getSelectedSensorPosition());
    //this is where I would use SetMinMaxOutput
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  //gets the value of an encoder from 0 to 420, we transfer negetive numbers to their positive counterpart
  public double currentEncoderCount()
  {
    double functreturn = rotationEncoder%ticksInRotation;
    return functreturn < 0 ? functreturn + ticksInRotation : functreturn;
  }

  /**sets the speed for the rotation motor
   * 
   * @param speed - (double) the desired power in percent [-1,1]
   * @return (void)
   */
  private void moveMotor(double speed)
  {
    rotationMotor.set(speed);
  }

  //stops the rotation motor
  private void stopMotors()
  {
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
  public void pointToTarget(double target)
  {
    //some local variables to use in calculations
    double flip = 0;
    double currentposition = currentEncoderCount();
    double desiredTarget = target;
    encoderRemainingValue = desiredTarget - (currentposition);
    double directionMultiplier = 0;

    
    if(encoderRemainingValue>ticksInRotation)
    {
      encoderRemainingValue -= ticksInRotation;
    }
    else if(encoderRemainingValue<-ticksInRotation)
    {
      encoderRemainingValue += ticksInRotation;
    }

    //preliminarily checking to see if it is at the value
    if((encoderRemainingValue%ticksInRotation != 0) || (-encoderRemainingValue%ticksInRotation != 0))
    {

      //checks to see the direction needed to go, basically the formula a/|a|
      // if we need to go to a positive direction a/|a| = 1
      // if we need to go negetive -a/|-a| = -1
      if(encoderRemainingValue>210)
      {
        directionMultiplier = Math.round((encoderRemainingValue-ticksInRotation)/Math.abs(encoderRemainingValue-ticksInRotation));
      }
      else if(encoderRemainingValue<210)
      {
        directionMultiplier = Math.round((encoderRemainingValue+ticksInRotation)/Math.abs(encoderRemainingValue+ticksInRotation));
      }
      else if(encoderRemainingValue < 210 && encoderRemainingValue > -210 && encoderRemainingValue != 0)
      {
        directionMultiplier = Math.round((encoderRemainingValue)/Math.abs(encoderRemainingValue));
      }
      else
      {
        directionMultiplier = 1;
      }

      // if(Math.abs(encoderRemainingValue)>105){
      //   if(flip > 0){
      //     flip-=210;
      //   }else{
      //     flip+=210;
      //   }
      // }

      //goes towards the point, if it is outside the large error it goes fast, if it is
      //in that range it goes at the slow speed untill smaller than the small error
      if(Math.abs(encoderRemainingValue) > Constants.LargeSwerveRotationError)
      {
        moveMotor(Constants.FastSwerveRotationSpeed*-directionMultiplier);
      }
      else if(Math.abs(encoderRemainingValue) > Constants.SmallSwerveRotationError)
      {
        moveMotor(Constants.SlowSwerveRotationSpeed*-directionMultiplier);
      }
      else
      {
        stopMotors();
      }
    }
  }

  //sets the encoder to zero
  public void zeroEncoder()
  {
    //encoderMotor.setSelectedSensorPosition(0);
  }

  //gets the encoder value but keeps it on a range from -420 to 420
  public void encoderValue()
  {
    double encoder = rotationEncoder;
    if(420 < Math.abs(encoder))
    {
      encoderMotor.setSelectedSensorPosition(rotationEncoder%ticksInRotation);
    }
  }

  //points the swerve to zero
  public void goToZero()
  {
    pointToTarget(0);
  }


  //function to make the unite move
  public void drive(double speed, double angle)
  {
    double revamp = speed;

    if(1 < Math.abs(speed)){
      revamp = (speed/Math.abs(speed));
    }

    driveMotor.set(revamp);

    if(angle<0)
    {
      /**I don't understand why I did this, *REWORK*
       * |-7+1|+1 = 7 but it would be the same without the +1's
       */
      pointSet = (Math.abs(1+angle)+1)*210;
    }else{
      pointSet = angle*210;
    }

    pointToTarget(pointSet);
  }

  public void staticAngle(double speed, double angle)
  {
    pointToTarget(angle);
    driveMotor.set(speed);
  }
}