package util;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.cornell.rocketry.util.StatusFlag;

public class StatusFlag_TEST {

	@Test
	public void test_none_set() {
		StatusFlag.Type[] args = 
			{StatusFlag.Type.gps_fix, 
				StatusFlag.Type.camera_enabled, 
				StatusFlag.Type.transmit_freq};
		
		StatusFlag f = new StatusFlag();

		for (StatusFlag.Type s : args) {
			assertFalse(f.isSet(s));
		}
	}
	
	@Test
	public void test_one_set() {
		StatusFlag.Type[] args = 
			{StatusFlag.Type.gps_fix, 
				StatusFlag.Type.camera_enabled, 
				StatusFlag.Type.transmit_freq};
		
		StatusFlag f = new StatusFlag();
		
		f.set(args[0], true);
		
		assertTrue(f.isSet(args[0]));
		
		for (int i = 1; i < args.length; i++) {
			assertFalse(f.isSet(args[i]));
		}
	}
	
	@Test
	public void test_all_set() {
		StatusFlag.Type[] args = 
			{StatusFlag.Type.gps_fix, 
				StatusFlag.Type.camera_enabled, 
				StatusFlag.Type.transmit_freq};
		
		StatusFlag f = new StatusFlag();
		
		for (StatusFlag.Type s : args) {
			f.set(s, true);
		}
		
		for (StatusFlag.Type s : args) {
			assertTrue(f.isSet(s));
		}
	}
	
	@Test
	public void test_unset() {
		StatusFlag.Type[] args = 
			{StatusFlag.Type.gps_fix, 
				StatusFlag.Type.camera_enabled, 
				StatusFlag.Type.transmit_freq};
		
		StatusFlag f = new StatusFlag();
		
		for (StatusFlag.Type s : args) {
			f.set(s, true);;
		}
		
		f.set(args[0], false);
		
		assertFalse(f.isSet(args[0]));
		
		for (int i = 1; i < args.length; i++) {
			assertTrue(f.isSet(args[i]));
		}
	}

}
