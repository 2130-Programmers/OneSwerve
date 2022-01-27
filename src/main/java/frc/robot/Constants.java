// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {

    
    public static double length = 30;

    public static double smallestTurn = .03;

    public static int TurretSpeedMaxOutput = 1;
    public static int TurretSpeedMinOutput = -1;
    public static boolean isTommyGay = true;

    public static double StickError = 0.05;

    public static int EncoderTicksInQuadrant = 1024; //105 per quad 420 per revolution
    public static int EncoderTicksInRevolution = 4096;
    public static int EncoderTicksInHalf = 2048;
    public static int EncoderticksInThreeQuarters = 3072;


    public static int LargeSwerveRotationError = 500; // change this to help jitter
    public static double SmallSwerveRotationError = 30; //Hard stop value
    public static double FastSwerveRotationSpeed = .2; //.4
    public static double SlowSwerveRotationSpeed = .05;  //.05

    public static double dirMMaxRotationOutput = 1;
    public static double dirMMinRotationOutput = -1;
}
