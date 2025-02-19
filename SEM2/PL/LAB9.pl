convertCL2DL([],LS,LS).
convertCL2DL([H|T],LS,LE) :- convertCL2DL(T, LS1 ,LE), LS = [H|LS1].

convertDL2CL(LS,LS,[]) :- var(LS),!.
convertDL2CL([H|T],LE,[H|R]) :- convertDL2CL(T,LE,R), LE=[].  

convertIL2DL(X, LS, LS) :- var(X),!.
convertIL2DL([H|T],LS,LE) :- convertIL2DL(T,LS1,LE), LS = [H|LS1].

convertDL2IL(LS,LS,_) :- var(LS), !.
convertDL2IL([H|T],LE,[H|R]) :- convertDL2IL(T,LE,R), LE = _.

                                    
                                          
preorder_dl(nil,L,L). 
preorder_dl(t(K,L,R),LS,LE):- preorder_dl(L, LS, [K|LT]),  preorder_dl(R, LT, LE).
 