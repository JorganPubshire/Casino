package Base;

import org.bukkit.Location;
import org.bukkit.block.Sign;

public class Slot {
	Sign sign1;
	Sign sign2;
	Location loc;
	int num;
	public Slot(Location loc2, Sign signA, Sign signB) {
		loc = loc2;
		sign1 = signA;
		sign2 = signB;
	}
	public void setLoc(Location loc2) {
		loc = loc2;
	}
	public Location getLoc(){
		return loc;
	}
	public void setSign(int numSign, Sign sign) {
		if(numSign == 1){
			sign1=sign;
		}
		else if(numSign == 2){
			sign2=sign;
		}
		else{
			return;
		}
	}
	public void clearSigns() {
		if(sign1 != null){
			sign1.setLine(0, "");
			sign1.setLine(1, "");
			sign1.setLine(2, "");
			sign1.setLine(3, "");
			sign1.update();
		}
		if(sign2 != null){
			sign2.setLine(0, "");
			sign2.setLine(1, "");
			sign2.setLine(2, "");
			sign2.setLine(3, "");
			sign2.update();
		}
	}
	public void write(int line, String string) {
		if(line>=4){
			return;
		}
		String[] message = string.split(" ");
		if(sign1 != null){
			sign1.setLine(line,message[1] + " " + message[2]);	
			sign1.update();
		}
		if(sign2 != null){
			sign2.setLine(line, message[3]);
			sign2.update();
		}
	}
	public void bettingSigns(int num){
		sign1.setLine(1,"[BET]");
		sign2.setLine(0,"Current Bet:");
		sign2.setLine(1, num + "");
		sign1.update();
		sign2.update();
	}
	public Sign getSign(int i) {
		switch(i){
		default: return null;
		case 1: return sign1;
		case 2: return sign2;
		}
	}
}
