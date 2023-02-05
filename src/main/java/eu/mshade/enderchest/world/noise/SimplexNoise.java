package eu.mshade.enderchest.world.noise;

public class SimplexNoise {
    // Simplex noise in 2D, 3D and 4D
    private long seed;

    public SimplexNoise(long seed) {
        this.seed = seed;
    }

    public float noise(double x, double y) {
        long n = (long) (x + y * 57L + seed);
        n = (n << 13) ^ n;
        return 1 - ((n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff) / 1073741824f;
    }

    public float noise(int x, int y, int z) {
        long n = x + y + z * 57L + seed;
        n = (n << 13) ^ n;
        return 1 - ((n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff) / 1073741824f;
    }

    public float smoothNoise(int x, int y) {
        float corners = (noise(x + 1, y + 1) + noise(x + 1, y - 1) + noise(x - 1, y + 1) + noise(x - 1, y - 1)) / 16f;
        float sides = (noise(x, y + 1) + noise(x, y - 1) + noise(x - 1, y) + noise(x + 1, y)) / 8f;
        float center = noise(x, y) / 4f;

        return corners + sides + center;
    }

    public float interpolatedNoise(float x, float y) {
        int integerX = (int) x;
        float fractionalX = x - integerX;

        int integerY = (int) y;
        float fractionalY = y - integerY;

        float v1 = smoothNoise(integerX, integerY);
        float v2 = smoothNoise(integerX + 1, integerY);
        float v3 = smoothNoise(integerX, integerY + 1);
        float v4 = smoothNoise(integerX + 1, integerY + 1);

        float i1 = interpolateCosine(v1, v2, fractionalX);
        float i2 = interpolateCosine(v3, v4, fractionalX);

        return interpolateCosine(i1, i2, fractionalY);
    }

    // x must be in the range [0,1]
    public float interpolateLinear(float a, float b, float x) {
        return a * (1 - x) + b * x;
    }

    // x must be in the range [0,1]
    public float interpolateCosine(float a, float b, float x) {
        float ft = x * (float) Math.PI;
        float f = (1 - ((float) Math.cos(ft))) * 0.5f;
        return a * (1 - f) + b * f;
    }

}
