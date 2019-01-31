push 0
lhp
lhp
lfp
push -3
lfp
add
lw
lfp
push function1
js
print
halt

function0:
cfp
lra
push 1
srv
sra
pop
sfp
lrv
lra
js

function1:
cfp
lra
lfp
lfp
lw
push function0
js
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
srv
sra
pop
sfp
lrv
lra
js

function3:
cfp
lra
lfp
lfp
lw
push function0
js
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
srv
sra
pop
sfp
lrv
lra
js

function5:
cfp
lra
lfp
lfp
lw
push function0
js
srv
sra
pop
pop
sfp
lrv
lra
js
