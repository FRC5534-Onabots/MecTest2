# MecTest2
Orginaly started out as a quick test to see if we could get Mecanum wheels working.  But since then has grown in to a testing ground for just about everything.  

## Tested so far
- [x] TalsonSRX over CAN loop control.
- [x] Driving with Gamepad controller.
- [x] Get gyro working (what a pain)

## Still to test
- [ ] Field Oriented driving.  Locking the robot on an angle, and have it stay on that angle while being driven around the field. 
- [ ] PID control on elevator hight. Being able to tell elevator to goto a set hight and stay there.
- [ ] PID control on grabber arm angle. 0 deg. and 180 deg. should be pretty simple.  Holding at 90 deg. will take the most work. But once we figure out PID control on the elevator, this should be pretty staight forward.
- [ ] FRCVision.  We have a lot of targets to track on the field.
  - [ ] Vision Targets on cargo ship, rocket, loading station.
  - [ ] White tape one floor for aligning robot up to targets
- [ ] The drag on the front right wheel. Can we fix this in code? With out fixing this, as it stands right now we can not drive stright for more then 10 feet.  

All of these items still to do should be entered as Issue in to GitHub.
