package util;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.cornell.rocketry.comm.TEMStatusFlag;

public class TEMStatusFlag_TEST {

	@Test
	public void test_none_set() {
		TEMStatusFlag.Type[] args = 
			{TEMStatusFlag.Type.gps_fix, 
				TEMStatusFlag.Type.camera_enabled, 
				TEMStatusFlag.Type.transmit_freq_max};
		
		TEMStatusFlag f = new TEMStatusFlag();

		for (TEMStatusFlag.Type s : args) {
			assertFalse(f.isSet(s));
		}
	}
	
	@Test
	public void test_one_set() {
		TEMStatusFlag.Type[] args = 
			{TEMStatusFlag.Type.gps_fix, 
				TEMStatusFlag.Type.camera_enabled, 
				TEMStatusFlag.Type.transmit_freq_max};
		
		TEMStatusFlag f = new TEMStatusFlag();
		
		f.set(args[0], true);
		
		assertTrue(f.isSet(args[0]));
		
		for (int i = 1; i < args.length; i++) {
			assertFalse(f.isSet(args[i]));
		}
	}
	
	@Test
	public void test_all_set() {
		TEMStatusFlag.Type[] args = 
			{TEMStatusFlag.Type.gps_fix, 
				TEMStatusFlag.Type.camera_enabled, 
				TEMStatusFlag.Type.transmit_freq_max};
		
		TEMStatusFlag f = new TEMStatusFlag();
		
		for (TEMStatusFlag.Type s : args) {
			f.set(s, true);
		}
		
		for (TEMStatusFlag.Type s : args) {
			assertTrue(f.isSet(s));
		}
	}
	
	@Test
	public void test_unset() {
		TEMStatusFlag.Type[] args = 
			{TEMStatusFlag.Type.gps_fix, 
				TEMStatusFlag.Type.camera_enabled, 
				TEMStatusFlag.Type.transmit_freq_max};
		
		TEMStatusFlag f = new TEMStatusFlag();
		
		for (TEMStatusFlag.Type s : args) {
			f.set(s, true);;
		}
		
		f.set(args[0], false);
		
		assertFalse(f.isSet(args[0]));
		
		for (int i = 1; i < args.length; i++) {
			assertTrue(f.isSet(args[i]));
		}
	}

}
