package cass.oli.simulation;

final public class Matrix {
	public int rows, columns;
	public Double[][] m;

	public Matrix(int r, int c) {
		this.rows = r;
		this.columns = c;
		m = new Double[r][c];
	}

	public Matrix(Double[][] m) {
		rows = m.length;
		columns = m[0].length;
		this.m = m;
	}

	public void add(double factor) {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++)
				m[i][j] += factor;
		}
	}

	public void multiply(double factor) {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++)
				m[i][j] *= factor;
		}
	}

	public void divide(double factor) {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++)
				m[i][j] /= factor;
		}
	}

	public int[][] round() {
		int[][] A = new int[rows][columns];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++)
				A[i][j] = (int) Math.round(m[i][j]);
		}
		return A;
	}

	public static Matrix multiply(Matrix A, Matrix B) {
		int aRows = A.m.length;
		int aColumns = A.m[0].length;
		int bRows = B.m.length;
		int bColumns = B.m[0].length;

		if (aColumns != bRows) {
			throw new IllegalArgumentException("A:Rows: " + aColumns + " did not match B:Columns " + bRows + ".");
		}

		Double[][] C = new Double[aRows][bColumns];
		for (int i = 0; i < aRows; i++) {
			for (int j = 0; j < bColumns; j++) {
				C[i][j] = 0.00000;
			}
		}

		for (int i = 0; i < aRows; i++) { // aRow
			for (int j = 0; j < bColumns; j++) { // bColumn
				for (int k = 0; k < aColumns; k++) { // aColumn
					C[i][j] += A.m[i][k] * B.m[k][j];
				}
			}
		}

		return new Matrix(C);
	}

	public static Matrix add(Matrix A, Matrix B) {
		if (A.rows != B.rows || A.columns != B.columns)
			throw new IllegalArgumentException("Matrix sizes did not match.");
		Matrix C = new Matrix(A.m);
		for (int i = 0; i < A.rows; i++) {
			for (int j = 0; j < B.columns; j++) {
				C.m[i][j] += B.m[i][j];
			}
		}
		return C;
	}

	public static Matrix subtract(Matrix A, Matrix B) {
		if (A.rows != B.rows || A.columns != B.columns)
			throw new IllegalArgumentException("Matrix sizes did not match.");
		Matrix C = new Matrix(A.m);
		for (int i = 0; i < A.rows; i++) {
			for (int j = 0; j < B.columns; j++) {
				C.m[i][j] -= B.m[i][j];
			}
		}
		return C;
	}

	public static Matrix dotMultiply(Matrix A, Matrix B) {
		if (A.rows != B.rows || A.columns != B.columns)
			throw new IllegalArgumentException("Matrix sizes did not match.");
		Matrix C = new Matrix(A.m);
		for (int i = 0; i < A.rows; i++) {
			for (int j = 0; j < B.columns; j++) {
				C.m[i][j] *= B.m[i][j];
			}
		}
		return C;
	}

	public static Matrix rotation(double theta) {
		return new Matrix(
				new Double[][] { { Math.cos(theta), -Math.sin(theta) }, { Math.sin(theta), Math.cos(theta) } });
	}
}
