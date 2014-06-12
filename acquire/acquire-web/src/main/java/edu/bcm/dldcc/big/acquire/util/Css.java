package edu.bcm.dldcc.big.acquire.util;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/* 10/3/13 CMW
 *  some PF components will not work with CSS rules, and instead require inline CSS
 *  In particular, selectOneMenu is one such example. This may be due to the way it is 
 *  skinned and requires additional client-side parsing before it's rendered. Either way,
 *  rather than code these values by hand, I'm storing them here in order to get the same
 *  effect as a CSS rule
 */

@Named
@ApplicationScoped
public class Css {
	//these numbers all take into account padding that is part of the selected PF theme
	//change the theme and padding, change these numbers. Based off 200px each width
	private String oneCol = "208px;";
	private String twoCol = "430px;";
	private String threeCol = "660px;";
	private String fourCol = "890px;";
	
	public String getOneCol() {
		return oneCol;
	}
	public void setOneCol(String oneCol) {
		this.oneCol = oneCol;
	}
	public String getTwoCol() {
		return twoCol;
	}
	public void setTwoCol(String twoCol) {
		this.twoCol = twoCol;
	}
	public String getThreeCol() {
		return threeCol;
	}
	public void setThreeCol(String threeCol) {
		this.threeCol = threeCol;
	}
	public String getFourCol() {
		return fourCol;
	}
	public void setFourCol(String fourCol) {
		this.fourCol = fourCol;
	}

}
