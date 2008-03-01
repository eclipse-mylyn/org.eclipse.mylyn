/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui;

class ColorCoordinatesChange {
	/**
	 * Change RGB colors to HSV colors
	 * 
	 * @param R
	 *            The red component of the color (0 - 255)
	 * @param G
	 *            The green component of the color (0 - 255)
	 * @param B
	 *            The blue component of the color (0 - 255)
	 * @return The HSV colors in an array of doubles. This means that position 0 = H, position 1 = S, and position 2 = V
	 */
	public static double[] RGBToHSV(double R, double G, double B) {
		double minVal = Math.min(Math.min(R, G), B);
		double V = Math.max(Math.max(R, G), B);

		double Delta = V - minVal;

		double S = 0;
		double H = 0;

		// Calculate saturation: saturation is 0 if r, g and b are all 0
		if (V == 0) {
			S = 0.0;
		} else {
			S = Delta / V;
		}

		if (S == 0) {
			H = 0; // Achromatic: When s = 0, h is undefined but who cares
		} else // Chromatic
		{
			if (R == V) {
				H = 60.0 * (G - B) / Delta;
			} else {
				if (G == V) {
					H = 120.0 + 60.0 * (B - R) / Delta;
				} else {
					// between magenta and cyan
					H = 240.0 + 60.0 * (R - G) / Delta;
				}

			}
		}

		if (H < 0) {
			H = H + 360.0;
		}
		// return a list of values as an rgb object would not be sensible
		return new double[] { H, S, V / 255.0 };
	}

	/**
	 * Change HSV colors to RGB colors
	 * 
	 * @param H
	 *            The hue of the color (0 - 360)
	 * @param S
	 *            The saturation of the color (0 - 1)
	 * @param V
	 *            The value of the color (0 - 1)
	 * @return The RGB colors in an array of ints. This means that position 0 = R, position 1 = G, and position 2 = B
	 */
	public static int[] HSVtoRGB(double H, double S, double V) {
		double R = 0, G = 0, B = 0;
		double hTemp, f, p, q, t;
		int i;
		if (S == 0) // color is on black-and-white center line
		{
			R = V; // achromatic: shades of gray
			G = V; // supposedly invalid for h=0 but who cares
			B = V;
		} else // chromatic color
		{
			if (H == 360.0) {
				hTemp = 0.0;
			} else {
				hTemp = H;
			}

			hTemp = hTemp / 60.0; // h is now in [0,6)
			i = new Double(hTemp).intValue(); // largest integer <= h
			f = hTemp - i; // fractional part of h

			p = V * (1.0 - S);
			q = V * (1.0 - (S * f));
			t = V * (1.0 - (S * (1.0 - f)));

			switch (i) {
			case 0:
				R = V;
				G = t;
				B = p;
				break;
			case 1:
				R = q;
				G = V;
				B = p;
				break;
			case 2:
				R = p;
				G = V;
				B = t;
				break;
			case 3:
				R = p;
				G = q;
				B = V;
				break;
			case 4:
				R = t;
				G = p;
				B = V;
				break;
			case 5:
				R = V;
				G = p;
				B = q;
				break;
			}
		}
		return new int[] { new Double(R * 255).intValue(), new Double(G * 255).intValue(),
				new Double(B * 255).intValue() };
	}

}
