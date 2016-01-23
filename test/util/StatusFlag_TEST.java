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
			assertFalse(f.is_set(s));
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
		
		assertTrue(f.is_set(args[0]));
		
		for (int i = 1; i < args.length; i++) {
			assertFalse(f.is_set(args[i]));
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
			assertTrue(f.is_set(s));
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
		
		assertFalse(f.is_set(args[0]));
		
		for (int i = 1; i < args.length; i++) {
			assertTrue(f.is_set(args[i]));
		}
	}

}
