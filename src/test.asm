push 0
lhp
push 5
lhp
sw
push 1
lhp
add
shp
lfp
push 5
lfp
push -2
add
lw
push 0
add
lw
lfp
push function4
js
print
halt

function0:
cfp
lra
push 1
lfp
add
lw
srv
sra
pop
pop
sfp
lrv
lra
js

function1:
cfp
lra
push 1
lfp
add
lw
push 3
mult
srv
sra
pop
pop
sfp
lrv
lra
js

function2:
cfp
lra
push 1
lfp
add
lw
srv
sra
pop
pop
sfp
lrv
lra
js

function3:
cfp
lra
push 1
lfp
add
lw
push 2
mult
srv
sra
pop
pop
sfp
lrv
lra
js

function4:
cfp
lra
push 1
lfp
add
lw
push 2
lfp
add
lw
mult
srv
sra
pop
pop
pop
sfp
lrv
lra
js
