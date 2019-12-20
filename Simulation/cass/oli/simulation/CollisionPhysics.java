package cass.oli.simulation;

public class CollisionPhysics {
	private static Collision temp = new Collision();

	public static void pointIntersectsRectangleOuter(float x, float y, float velX, float velY, float radius, float x1,
			float y1, float x2, float y2, float timeLimit, Collision response) {
		response.reset();

		// Right Border
		pointIntersectsLineVertical(x, y, velX, velY, radius, x2, timeLimit, temp);
		if (temp.t < response.t)
			response.copy(temp);

		// Left Border
		pointIntersectsLineVertical(x, y, velX, velY, radius, x1, timeLimit, temp);
		if (temp.t < response.t)
			response.copy(temp);

		// Top Border
		pointIntersectsLineHorizontal(x, y, velX, velY, radius, y1, timeLimit, temp);
		if (temp.t < response.t)
			response.copy(temp);

		// Bottom Border
		pointIntersectsLineHorizontal(x, y, velX, velY, radius, y2, timeLimit, temp);
		if (temp.t < response.t)
			response.copy(temp);
	}

	public static void pointIntersectsLineVertical(float x, float y, float velX, float velY, float radius, float lineX,
			float timeLimit, Collision response) {
		response.reset();

		if (velX == 0) {
			return;
		}

		float distance;
		if (lineX > x)
			distance = lineX - x - radius;
		else
			distance = lineX - x + radius;

		float t = distance / velX;
		if (t > 0 && t <= timeLimit) {
			response.t = t;
			response.nVelX = -velX;
			response.nVelY = velY;
		}
	}

	public static void pointIntersectsLineHorizontal(float x, float y, float velX, float velY, float radius,
			float lineY, float timeLimit, Collision response) {
		response.reset();

		if (velY == 0) {
			return;
		}

		float distance;
		if (lineY > y)
			distance = lineY - y - radius;
		else
			distance = lineY - y + radius;

		float t = distance / velY;
		if (t > 0 && t <= timeLimit) {
			response.t = t;
			response.nVelX = velX;
			response.nVelY = -velY;
		}
	}

	public static void pointIntersectsMovingPoint(float p1X, float p1Y, float p1SpeedX, float p1SpeedY, float p1Radius,
			float p2X, float p2Y, float p2SpeedX, float p2SpeedY, float p2Radius, float timeLimit, Collision p1Response,
			Collision p2Response) {

		p1Response.reset();
		p2Response.reset();

		// Call helper method to compute the collision time t.
		float t = pointIntersectsMovingPointDetection(p1X, p1Y, p1SpeedX, p1SpeedY, p1Radius, p2X, p2Y, p2SpeedX,
				p2SpeedY, p2Radius);

		// Accept 0 < t <= timeLimit
		if (t > 0 && t <= timeLimit) {
			// Call helper method to compute the responses in the 2 Response objects
			pointIntersectsMovingPointResponse(p1X, p1Y, p1SpeedX, p1SpeedY, p1Radius, p2X, p2Y, p2SpeedX, p2SpeedY,
					p2Radius, p1Response, p2Response, t);
		}
	}

	private static float pointIntersectsMovingPointDetection(float p1X, float p1Y, float p1SpeedX, float p1SpeedY,
			float p1Radius, float p2X, float p2Y, float p2SpeedX, float p2SpeedY, float p2Radius) {

		// Rearrange the parameters to set up the quadratic equation.
		double centerX = p1X - p2X;
		double centerY = p1Y - p2Y;
		double speedX = p1SpeedX - p2SpeedX;
		double speedY = p1SpeedY - p2SpeedY;
		double radius = p1Radius + p2Radius;
		double radiusSq = radius * radius;
		double speedXSq = speedX * speedX;
		double speedYSq = speedY * speedY;
		double speedSq = speedXSq + speedYSq;

		// Solve quadratic equation for collision time t
		double termB2minus4ac = radiusSq * speedSq
				- (centerX * speedY - centerY * speedX) * (centerX * speedY - centerY * speedX);
		if (termB2minus4ac < 0) {
			// No intersection.
			// Moving spheres may cross at different times, or move in parallel.
			return Float.MAX_VALUE;
		}

		double termMinusB = -speedX * centerX - speedY * centerY;
		double term2a = speedSq;
		double rootB2minus4ac = Math.sqrt(termB2minus4ac);
		double sol1 = (termMinusB + rootB2minus4ac) / term2a;
		double sol2 = (termMinusB - rootB2minus4ac) / term2a;
		// Accept the smallest positive t as the solution.
		if (sol1 > 0 && sol2 > 0) {
			return (float) Math.min(sol1, sol2);
		} else if (sol1 > 0) {
			return (float) sol1;
		} else if (sol2 > 0) {
			return (float) sol2;
		} else {
			// No positive t solution. Set detected collision time to infinity.
			return Float.MAX_VALUE;
		}
	}

	private static void pointIntersectsMovingPointResponse(float p1X, float p1Y, float p1SpeedX, float p1SpeedY,
			float p1Radius, float p2X, float p2Y, float p2SpeedX, float p2SpeedY, float p2Radius, Collision p1Response,
			Collision p2Response, float t) {

		// Update the detected collision time in CollisionResponse.
		p1Response.t = t;
		p2Response.t = t;

		// Get the point of impact, to form the line of collision.
		double p1ImpactX = p1Response.getImpactX(p1X, p1SpeedX);
		double p1ImpactY = p1Response.getImpactY(p1Y, p1SpeedY);
		double p2ImpactX = p2Response.getImpactX(p2X, p2SpeedX);
		double p2ImpactY = p2Response.getImpactY(p2Y, p2SpeedY);

		// Direction along the line of collision is P, normal is N.
		// Get the direction along the line of collision
		double lineAngle = Math.atan2(p2ImpactY - p1ImpactY, p2ImpactX - p1ImpactX);

		// Project velocities from (x, y) to (p, n)
		double[] result = rotate(p1SpeedX, p1SpeedY, lineAngle);
		double p1SpeedP = result[0];
		double p1SpeedN = result[1];
		result = rotate(p2SpeedX, p2SpeedY, lineAngle);
		double p2SpeedP = result[0];
		double p2SpeedN = result[1];

		// Collision possible only if p1SpeedP - p2SpeedP > 0
		// Needed if the two balls overlap in their initial positions
		// Do not declare collision, so that they continue their course of movement
		// until they are separated.
		if (p1SpeedP - p2SpeedP <= 0) {
			// System.out.println("velocities cannot collide! t = " + t);
			p1Response.reset(); // Set collision time to infinity
			p2Response.reset();
			return;
		}

		// Assume that mass is proportional to the cube of radius.
		// (All objects have the same density.)
		double p1Mass = p1Radius * p1Radius * p1Radius;
		double p2Mass = p2Radius * p2Radius * p2Radius;
		double diffMass = p1Mass - p2Mass;
		double sumMass = p1Mass + p2Mass;

		double p1SpeedPAfter, p1SpeedNAfter, p2SpeedPAfter, p2SpeedNAfter;
		// Along the collision direction P, apply conservation of energy and momentum
		p1SpeedPAfter = (diffMass * p1SpeedP + 2.0 * p2Mass * p2SpeedP) / sumMass;
		p2SpeedPAfter = (2.0 * p1Mass * p1SpeedP - diffMass * p2SpeedP) / sumMass;

		// No change in the perpendicular direction N
		p1SpeedNAfter = p1SpeedN;
		p2SpeedNAfter = p2SpeedN;

		// Project the velocities back from (p, n) to (x, y)
		result = rotate(p1SpeedPAfter, p1SpeedNAfter, -lineAngle);
		p1Response.nVelX = (float) result[0];
		p1Response.nVelY = (float) result[1];
		result = rotate(p2SpeedPAfter, p2SpeedNAfter, -lineAngle);
		p2Response.nVelX = (float) result[0];
		p2Response.nVelY = (float) result[1];
	}

	private static double[] rotateResult = new double[2];

	private static double[] rotate(double x, double y, double theta) {
		double sinTheta = Math.sin(theta);
		double cosTheta = Math.cos(theta);
		rotateResult[0] = x * cosTheta + y * sinTheta;
		rotateResult[1] = -x * sinTheta + y * cosTheta;
		return rotateResult;
	}
}
