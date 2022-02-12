// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.motorcontrol.Victor;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Intake extends SubsystemBase {
  /** Creates a new Intake. */
  public Intake() {}
  Victor feedMotor = new Victor(0);
  Solenoid firingSolenoid = new Solenoid(PneumaticsModuleType.CTREPCM, 0);

    /**
   * Runs feed motor at specified speed
   *
   * @param speed If set to 2, runs at default speed, otherwise runs at set speed 
   */
  public void runFeed(double speed) {
    if(speed != 2) {
        feedMotor.set(speed);
    }else{
        feedMotor.set(.25);
    }
  }

  //Stops ball feed
  public void stopFeed() {
    feedMotor.set(0);
  }




  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
