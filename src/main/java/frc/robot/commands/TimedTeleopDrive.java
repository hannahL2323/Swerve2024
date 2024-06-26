// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.subsystems.SwerveSubsystem;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import swervelib.SwerveController;

public class TimedTeleopDrive extends Command {

  private final SwerveSubsystem  swerve;
  private final DoubleSupplier   vX;
  private final DoubleSupplier   vY;
  private final DoubleSupplier   omega;
  private final BooleanSupplier  driveMode;
  private final SwerveController controller;
  
  long start;
  long duration;
  long currentTime;


  /** Creates a new TimedTeleopDrive. */
  public TimedTeleopDrive(SwerveSubsystem swerve, DoubleSupplier vX, DoubleSupplier vY, DoubleSupplier omega,
  BooleanSupplier driveMode, long duration) {

    this.swerve = swerve;
    this.vX = vX;
    this.vY = vY;
    this.omega = omega;
    this.driveMode = driveMode;
    this.controller = swerve.getSwerveController();
    
    this.duration = duration;

    addRequirements(swerve);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    start = System.currentTimeMillis();

  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    double xVelocity   = Math.pow(vX.getAsDouble(), 3);
    double yVelocity   = Math.pow(vY.getAsDouble(), 3);
    double angVelocity = Math.pow(omega.getAsDouble(), 3);
    SmartDashboard.putNumber("vX", xVelocity);
    SmartDashboard.putNumber("vY", yVelocity);
    SmartDashboard.putNumber("omega", angVelocity);

    // Drive using raw values.
    swerve.drive(new Translation2d(xVelocity * swerve.maximumSpeed, yVelocity * swerve.maximumSpeed),
                 angVelocity * controller.config.maxAngularVelocity,
                 driveMode.getAsBoolean());

    currentTime = System.currentTimeMillis();

  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
   swerve.drive(new Translation2d(0, 0),
                 0,
                 driveMode.getAsBoolean());
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    if (start + duration <= currentTime) {
      return true;
    }
    return false;
  }
}
