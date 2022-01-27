// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Robot;
import frc.robot.RobotContainer;

public class DriveTrain extends SubsystemBase {
  /** Creates a new DriveTrain. */
/**
  public double driverJoyLeftX;
  private double driverJoyLeftY;
  private double driverJoyRightX;

  private double leftXOutput;
  private double leftYOutput;
  private double rightXOutput;
*/

  //defining swerve units as our SwerveMotor class
  public SwerveMotor motorFL;

  //creating a variable which will be our angle
  public double FLAngle = 0;

  //for the side length of the robot
  //it is undefined in constants rn
  public double l;
  //r is the hypotenuse and will be used for *vector math*
  public double r;


  //defining each swerve unit with the custom swerve class we made
  public DriveTrain() 
  {
    //driverJoyLeftX = RobotContainer.driverJoy.getRawAxis(0);
    //driverJoyLeftY = RobotContainer.driverJoy.getRawAxis(1);
    //driverJoyRightX = RobotContainer.driverJoy.getRawAxis(4);
/**
    if(driverJoyLeftX < .05){
      leftXOutput = 0;
    } else{
      leftXOutput = RobotContainer.driverJoy.getRawAxis(0);
    }

    if(driverJoyLeftY < .05){
      leftYOutput = 0;
    } else{
      leftYOutput = RobotContainer.driverJoy.getRawAxis(0);
    }

    if(driverJoyRightX < .05){
      rightXOutput = 0;
    } else{
      rightXOutput = RobotContainer.driverJoy.getRawAxis(0);
    }
*/

  
    //we need to change the parameters, based off of Swolenoid
    motorFL = new SwerveMotor(1, 2, 3);

    //defining l by using constants, constants are good for these types of variables.
    //You can find constants in the editor right below the subsystems
    l = Constants.length;
    //this is a formula for hypotenuse with a 45 45 90 triangle
    r = (Math.sqrt(2)*l);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  //for calculating which way the swerve units should point and how much power should be given
  public void moveSwerveAxis(double leftX, double leftY, double rightX)
  {
    double swivel = rightX * (l / r);
    // a b c and d are the sides of the robot, wheels are made from the combination of sides
    /**
     * this is the beginning of the vector based math, and in short we are calculating where each side wants to be
     * and then by combining the two sides into a triangle it gives us a swerve unit. For example The front left 
     * swerve unit is unit BC, because in a triangle made from the robots frames, the two sides are b and c.
     * This part is simply saying where each side wants to go by themselves.
     */
    //double b = leftX+swivel;
    //double c = rightX-swivel;
    double b = leftX+swivel;
    double c = leftY-swivel;
    //calculates the speed based on *vector math*
    /**
     */
    double FLDesiredSpeed = -Math.sqrt((b*b)+(c*c));

    //notorious problem area, rework if possible, atan will return zero and not tell you
    if(leftX == 0 && leftY == 0 && swivel == 0)
    {
      //tries to avoid divide by zero error
      FLAngle = 0;
    }else{
      //calculates the angle based of where each side wants to go
      FLAngle = Math.atan2(b, c)/Math.PI;
    }

    motorFL.drive(FLDesiredSpeed, FLAngle);
  }

  //reaching into each motors "SwerveMotor" class to zero encoders
  public void zeroAllEncoders()
  {
    motorFL.zeroEncoder();
  }


  //Pressing Alt+left Click will duplicate your curser 
  //so you can type in multiple spots at once
  public void findZero()
  {
    motorFL.goToZero();
  }


}
