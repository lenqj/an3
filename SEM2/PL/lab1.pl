woman(ana).
woman(sara).
woman(ema).
woman(maria).
woman(carmen).
woman(dorina).
woman(irina).
man(andrei).
man(george).
man(alex).
man(marius).
man(mihai).
man(sergiu).
parent(maria, ana).
parent(george, ana).
parent(maria, andrei).
parent(george, andrei).
parent(carmen, sara).
parent(alex, sara).
parent(alex, ema).
parent(carmen, ema).
parent(marius, maria).
parent(dorina, maria).
parent(mihai, george).
parent(irina, george).
parent(mihai, carmen).
parent(irina, carmen).
parent(sergiu, mihai).
mother(X,Y) :- woman(X), parent(X,Y).
father(X,Y) :- man(X), parent(X,Y).
sibling(X,Y) :- parent(Z,X), parent(Z,Y), X\=Y.
sister(X,Y) :- sibling(X,Y), woman(X).
aunt(X,Y) :- sister(X,Z), parent(Z,Y).
brother(X,Y) :- sibling(X,Y), man(X).
uncle(X,Y) :- brother(X,Z), parent(Z,Y).
grandmother(X, Y) :- parent(X, Z), parent(Z, Y), woman(X), X\=Y,  X\=Z.
grandfather(X, Y) :- parent(X, Z), parent(Z, Y), man(X), X\=Y,  X\=Z.
ancestor(X, Y) :- parent(X, Y).
ancestor(X, Y) :- parent(Z, Y), ancestor(X, Z).