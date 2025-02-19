cmmdc1(X,X,X).
cmmdc1(X,Y,Z) :- X>Y, Diff is X-Y, cmmdc1(Diff,Y,Z). 
cmmdc1(X,Y,Z) :- X<Y, Diff is Y-X, cmmdc1(X,Diff,Z). 

cmmc(X, Y, Z) :- P is X * Y, cmmdc1(X, Y, CMD), Z is P/CMD.

power_fwd(_, 0, Acc, Acc).
power_fwd(X, Y, Z, Acc) :- Y > 0, Y1 is Y - 1, Acc1 is Acc*X, power_fwd(X, Y1, Z, Acc1).
power_fwd(X, Y, Z) :- power_fwd(X, Y, Z, 1).

power_bwd(_, 0, 1).
power_bwd(X, Y, Z) :- Y > 0, Y1 is Y - 1, power_bwd(X, Y1, Z1), Z is X*Z1.


fib(0, 0).
fib(1, 1).
fib(N, R) :- N>0, N1 is N - 1, N2 is N - 2, fib(N1, R1), fib(N2, R2), R is R1 + R2.


triangle(A, B, C) :- AB is A + B, AC is A + C, BC is B + C, AB > C, AC > B, BC > A.

solve(A, B, C, X) :- DELTA is (B**2 - 4*A*C), (DELTA = 0, X is (-B + sqrt(DELTA))/2*A), (DELTA > 0, X is (-B - sqrt(DELTA))/2*A).



solve(A, B, C, X) :- DELTA is (B**2 - 4*A*C), DELTA = 0, X is (-B + sqrt(DELTA))/2*A.
solve(A, B, C, X) :- DELTA is (B**2 - 4*A*C), DELTA > 0, X is (-B - sqrt(DELTA))/2*A.