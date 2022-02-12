// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;


import java.security.spec.EncodedKeySpec;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class SwerveMotor extends SubsystemBase {
  /** Creates a new SwerveMotor. */


  //creating our motors and encoder
  public TalonFX driveMotor; //controls the drive motor
  public TalonFX rotationMotor; //controls the rotation of the unit
  public TalonSRX encoderMotor; // for encoder
  public static double sDencoderRemainingValue = 0;
  public static double sdPointSet = 0;
  public static double sdPointSetMod = 0;
  public static double sdCurrentEncoderCount = 0;
  public double encoderPosition = 0;
  public double rawEncoderPosition = 0;
  public double turnPowerRatio = 0;

  public static boolean testBoolean;

  //creating variables
  public static double rotationEncoder = 0;
  public double encoderRemainingValue = 0;
  public double pointSet = 0;
  public double pointSetMod = 0;
  public double reverse = 0;
  public double encoderVal = 0;

  public double ticksInRotation = Constants.EncoderTicksInRevolution; //changed from quadrent to revolution
  public double ticksInHalf = Constants.EncoderTicksInHalf;
  public double ticksInThreeQuarters = Constants.EncoderticksInThreeQuarters;
  public double rotationSpeedCap = Constants.RotationSpeedCap;
  
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
    rotationMotor = new TalonFX(rotate);
    driveMotor = new TalonFX(drive);
    encoderMotor = new TalonSRX(encoder);
    testBoolean = false;
    
    //encoderPosition = encoderMotor.getSelectedSensorPosition() % ticksInRotation;

    //rotationEncoder = (encoderMotor.getSelectedSensorPosition());
    //this is where I would use SetMinMaxOutput
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  //gets the value of an encoder from 0 to 420, we transfer negetive numbers to their positive counterpart
  public double currentEncoderCount()
  {
    double functreturn = encoderPosition;
    return functreturn;
  }

  /**sets the speed for the rotation motor
   * 
   * @param speed - (double) the desired power in percent [-1,1]
   * @return (void)
   */
  private void moveMotor(double speed)
  {
    testBoolean = true;
    rotationMotor.set(ControlMode.PercentOutput, speed);
    //encoderMotor.setSelectedSensorPosition(0);
  }

  //stops the rotation motor
  private void stopMotors()
  {
    rotationMotor.set(ControlMode.PercentOutput, 0);
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
    rawEncoderPosition = encoderMotor.getSelectedSensorPosition(0);

    if (rawEncoderPosition >= 0)
    {
      encoderPosition = encoderMotor.getSelectedSensorPosition(0) % ticksInRotation;
    }
      else
    {
      encoderPosition = (encoderMotor.getSelectedSensorPosition(0) % ticksInRotation) + 4096;
    }
    //double flip = 0;
    double currentposition = currentEncoderCount();

    double desiredTarget = target;
    encoderRemainingValue = desiredTarget - currentposition;
    double directionMultiplier = 0;

    //preliminarily checking to see if it is at the value
if((encoderRemainingValue % ticksInRotation != 0) || (-encoderRemainingValue % ticksInRotation != 0))
    {

      //checks to see the direction needed to go, basically the formula a/|a|
      // if we need to go to a positive direction a/|a| = 1


      if(encoderRemainingValue > ticksInThreeQuarters)
{
  directionMultiplier = Math.round(encoderRemainingValue) / Math.abs(encoderRemainingValue);//-
}
else if(encoderRemainingValue < ticksInHalf && encoderRemainingValue > -ticksInHalf && encoderRemainingValue != 0)
{
  directionMultiplier = Math.round((encoderRemainingValue) / Math.abs(encoderRemainingValue)) * -1; //+
 }
 else if(encoderRemainingValue > ticksInHalf)
{
  directionMultiplier = Math.round((encoderRemainingValue - ticksInRotation) / Math.abs(encoderRemainingValue - ticksInRotation)) * -1;//+
}
else if(encoderRemainingValue < -ticksInHalf)
{
  directionMultiplier = Math.round((encoderRemainingValue+ticksInRotation) / Math.abs(encoderRemainingValue+ticksInRotation)) * -1;//+
}
else
{
  directionMultiplier = 1;
}

sDencoderRemainingValue = encoderRemainingValue;
      // if(Math.abs(encoderRemainingValue)>105){
      //   if(flip > 0){
      //     flip-=210;
      //   }else{
      //     flip+=210;
      //   }
      // }

      //goes towards the point, if it is outside the large error it goes fast, if it is
      //in that range it goes at the slow speed untill smaller than the small error

      //turnPowerRatio = .000004 * Math.pow(encoderRemainingValue, 2) + .00002 * encoderRemainingValue +.0202;
      double turnPowerRatio = (((rotationSpeedCap) / 500) * Math.abs(encoderRemainingValue));

      if (Math.abs(encoderRemainingValue) > 500) {
        moveMotor(rotationSpeedCap * -directionMultiplier);
      }else if (Math.abs(encoderRemainingValue) < 500 && Math.abs(encoderRemainingValue) > 40) {
        moveMotor(turnPowerRatio * -directionMultiplier);
      } else  {
        stopMotors();
      }
      
      
    }
  }

  //sets the encoder to zero
  public void zeroEncoder()
  {
    encoderMotor.setSelectedSensorPosition(0);
  }

  //gets the encoder value but keeps it on a range from -420 to 420
  //public void encoderValue()
  //{
  //  double encoder = rotationEncoder;
  //  if(420 < Math.abs(encoder))
  //  {
  //    encoderMotor.setSelectedSensorPosition(rotationEncoder % ticksInRotation);
  //  }
 // }

  //points the swerve to zero
  public void goToZero()
  {
    pointToTarget(0);
  }


  //function to make the unite move
  public void drive(double speed, double angle)
  {
    sdPointSet = pointSet;
    sdPointSetMod = pointSetMod;
    double revamp = speed;
    if(1 < Math.abs(speed)){
      revamp = (speed/Math.abs(speed));
    }

    driveMotor.set(ControlMode.PercentOutput, revamp * .7);

      /**I don't understand why I did this, *REWORK*
       * |-7+1|+1 = 7 but it would be the same without the +1's
       */

      //pointSet = (Math.abs(1+angle)+1) * ticksInHalf;
      if (angle < 0)
      {
        pointSet = ticksInRotation + (angle * ticksInHalf);
      } else if (angle >= 0)
      {
        pointSet = angle * ticksInHalf;
      } else
      {
        stopMotors();
        //pointSet = 0;
      }
  // POINTSETMOD is not being used, it swaps the polarity of the desired target and makes it count up from the bottom isntead of the top
      pointSetMod = pointSet;
      pointSetMod -= ticksInHalf;

      if (pointSetMod > 0){
      } else if(pointSetMod < 0){
        pointSetMod += ticksInRotation;
      } else {
      pointSetMod = 0;
      }

    pointToTarget(pointSet);
  }

  public void staticAngle(double speed, double angle)
  {
    pointToTarget(angle);
    driveMotor.set(ControlMode.PercentOutput, speed);
  }
  public double SmartDashboardReaderEncoder() {
    return sDencoderRemainingValue;
  }
  public double SmartDashboardReaderPointSet() {
    return sdPointSet;
  }
    public static double SmartDashboardReaderEncoderPos() {
    return sdCurrentEncoderCount;
    }
  public double SmartDashboardReaderPointSetMod() {
    return sdPointSetMod;
  }
  
}