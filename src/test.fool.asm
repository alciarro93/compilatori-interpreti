push 0
lhp
push 4
lhp
sw
push 1
lhp
add
shp
push 77
lhp
sw
push 1
lhp
add
shp
push 0
lhp
sw
push 1
lhp
add
shp
push 0
lhp
sw
push 1
lhp
add
shp
lhp
lfp
push 10
push -2
lfp
add
lw
lfp
push -2
add
lw
push 3
add
lw
lfp
push -2
add
lw
push 2
add
lw
lfp
push -2
add
lw
push 1
add
lw
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
pop
lhp
push 6
lhp
sw
push 1
lhp
add
shp
push 5
lhp
sw
push 1
lhp
add
shp
push 0
lhp
sw
push 1
lhp
add
shp
push 0
lhp
sw
push 1
lhp
add
shp
push 0
lhp
sw
push 1
lhp
add
shp
lhp
push 8
lhp
sw
push 1
lhp
add
shp
push 5
lhp
sw
push 1
lhp
add
shp
push 0
lhp
sw
push 1
lhp
add
shp
push 0
lhp
sw
push 1
lhp
add
shp
lhp
push 5
lhp
sw
push 1
lhp
add
shp
push 5
lhp
sw
push 1
lhp
add
shp
push 0
lhp
sw
push 1
lhp
add
shp
push 0
lhp
sw
push 1
lhp
add
shp
lhp
sw
push 1
lhp
add
shp
push 5
lhp
sw
push 1
lhp
add
shp
lfp
push 5
push 5
lfp
push -5
add
lw
push 3
add
lw
lfp
push -5
add
lw
push 2
add
lw
lfp
push -5
add
lw
push 1
add
lw
lfp
push -5
add
lw
push 0
add
lw
lfp
push function9
js
lfp
push 4
push 4
lfp
push -3
add
lw
push 4
add
lw
lfp
push -3
add
lw
push 3
add
lw
lfp
push -3
add
lw
push 2
add
lw
lfp
push -3
add
lw
push 1
add
lw
lfp
push -3
add
lw
push 0
add
lw
lfp
push function20
js
lfp
push 4
push 4
lfp
push -4
add
lw
push 4
add
lw
lfp
push -4
add
lw
push 3
add
lw
lfp
push -4
add
lw
push 2
add
lw
lfp
push -4
add
lw
push 1
add
lw
lfp
push -4
add
lw
push 0
add
lw
lfp
push function21
js
mult
lfp
push -2
lfp
add
lw
lfp
push -6
add
lw
push 1
add
lw
lfp
push -6
add
lw
push 0
add
lw
lfp
push function23
js
add
add
print
halt

function0:
cfp
lra
push 0
push 3
lfp
add
lw
push 1
lfp
add
lw
push 2
lfp
add
lw
add
add
srv
pop
sra
pop
pop
pop
pop
pop
pop
sfp
lrv
lra
js

function1:
cfp
lra
push 0
push 2
srv
pop
sra
pop
pop
pop
pop
pop
sfp
lrv
lra
js

function2:
cfp
lra
push 4
push 4
add
srv
sra
pop
pop
pop
sfp
lrv
lra
js

function3:
cfp
lra
push function2
push 1
push 10
add
srv
pop
sra
pop
pop
pop
pop
pop
sfp
lrv
lra
js

function4:
cfp
lra
lhp
push 4
lhp
sw
push 1
lhp
add
shp
push 7
lhp
sw
push 1
lhp
add
shp
push 0
lhp
sw
push 1
lhp
add
shp
push 0
lhp
sw
push 1
lhp
add
shp
push 0
lhp
sw
push 1
lhp
add
shp
lfp
push -2
add
lw
push -2
lfp
add
lw
srv
pop
pop
sra
pop
pop
pop
pop
pop
pop
pop
sfp
lrv
lra
js

function5:
cfp
lra
lhp
push 4
lhp
sw
push 1
lhp
add
shp
push 4
lhp
sw
push 1
lhp
add
shp
push 0
lhp
sw
push 1
lhp
add
shp
push 0
lhp
sw
push 1
lhp
add
shp
push 6
srv
pop
sra
pop
pop
pop
pop
pop
pop
pop
sfp
lrv
lra
js

function6:
cfp
lra
push 0
push 5
lfp
add
lw
push 4
lfp
add
lw
push 3
lfp
add
lw
add
add
srv
pop
sra
pop
pop
pop
pop
pop
pop
pop
pop
sfp
lrv
lra
js

function7:
cfp
lra
push 5
push 5
add
srv
sra
pop
sfp
lrv
lra
js

function8:
cfp
lra
push function7
lfp
lfp
push -2
lfp
add
lw
js
srv
pop
sra
pop
pop
pop
sfp
lrv
lra
js

function9:
cfp
lra
push function8
lfp
push 5
push 5
lfp
push -2
lfp
add
lw
js
lfp
push 5
push 5
lfp
push -2
lfp
add
lw
js
lfp
push 5
push 5
push 5
lfp
push 1
add
push 3
add
lw
lfp
push 1
add
push 2
add
lw
lfp
push 1
add
push 1
add
lw
lfp
push 1
add
push 0
add
lw
lfp
lw
push function6
js
mult
srv
pop
pop
sra
pop
pop
pop
pop
pop
pop
pop
sfp
lrv
lra
js

function10:
cfp
lra
push 4
push 4
add
srv
sra
pop
pop
pop
sfp
lrv
lra
js

function11:
cfp
lra
push function10
push 1
push 10
add
srv
pop
sra
pop
pop
pop
pop
pop
pop
pop
sfp
lrv
lra
js

function12:
cfp
lra
push 0
push 6
lfp
add
lw
push 1
lfp
add
lw
push 3
lfp
add
lw
add
add
srv
pop
sra
pop
pop
pop
pop
pop
pop
pop
pop
pop
sfp
lrv
lra
js

function13:
cfp
lra
push 4
push 4
add
srv
sra
pop
pop
pop
sfp
lrv
lra
js

function14:
cfp
lra
push function13
push 1
push 10
add
srv
pop
sra
pop
pop
pop
pop
pop
pop
pop
pop
sfp
lrv
lra
js

function15:
cfp
lra
push 5
push 5
add
srv
sra
pop
sfp
lrv
lra
js

function16:
cfp
lra
push function15
lfp
lfp
push -2
lfp
add
lw
js
srv
pop
sra
pop
pop
pop
sfp
lrv
lra
js

function17:
cfp
lra
push function16
lfp
push 5
push 5
lfp
push -2
lfp
add
lw
js
lfp
push 5
push 5
lfp
push -2
lfp
add
lw
js
lfp
push 5
push 5
push 5
lfp
push 1
add
push 4
add
lw
lfp
push 1
add
push 3
add
lw
lfp
push 1
add
push 2
add
lw
lfp
push 1
add
push 1
add
lw
lfp
push 1
add
push 0
add
lw
lfp
lw
push function12
js
mult
srv
pop
pop
sra
pop
pop
pop
pop
pop
pop
pop
pop
sfp
lrv
lra
js

function18:
cfp
lra
lhp
push 4
lhp
sw
push 1
lhp
add
shp
push 7
lhp
sw
push 1
lhp
add
shp
push 0
lhp
sw
push 1
lhp
add
shp
push 0
lhp
sw
push 1
lhp
add
shp
push 0
lhp
sw
push 1
lhp
add
shp
lfp
push -2
add
lw
push -2
lfp
add
lw
srv
pop
pop
sra
pop
pop
pop
pop
pop
pop
pop
pop
sfp
lrv
lra
js

function19:
cfp
lra
push 25
push 6
srv
pop
sra
pop
pop
pop
pop
pop
pop
pop
sfp
lrv
lra
js

function20:
cfp
lra
lhp
push 4
lhp
sw
push 1
lhp
add
shp
push 4
lhp
sw
push 1
lhp
add
shp
push 0
lhp
sw
push 1
lhp
add
shp
push 0
lhp
sw
push 1
lhp
add
shp
push 2
lfp
add
lw
srv
pop
sra
pop
pop
pop
pop
pop
pop
pop
pop
sfp
lrv
lra
js

function21:
cfp
lra
lhp
push 4
lhp
sw
push 1
lhp
add
shp
push 4
lhp
sw
push 1
lhp
add
shp
lhp
push 4
lhp
sw
push 1
lhp
add
shp
push 4
lhp
sw
push 1
lhp
add
shp
push 0
lhp
sw
push 1
lhp
add
shp
push 0
lhp
sw
push 1
lhp
add
shp
push 0
lhp
sw
push 1
lhp
add
shp
lfp
push 8
push 6
lfp
push -3
add
lw
push 4
add
lw
lfp
push -3
add
lw
push 3
add
lw
lfp
push -3
add
lw
push 2
add
lw
lfp
push -3
add
lw
push 1
add
lw
lfp
push -3
add
lw
push 0
add
lw
lfp
lw
push function20
js
lfp
push 8
push 6
lfp
push 1
add
push 4
add
lw
lfp
push 1
add
push 3
add
lw
lfp
push 1
add
push 2
add
lw
lfp
push 1
add
push 1
add
lw
lfp
push 1
add
push 0
add
lw
lfp
lw
push function14
js
push -4
lfp
add
lw
push -5
lfp
add
lw
add
srv
pop
pop
pop
pop
sra
pop
pop
pop
pop
pop
pop
pop
pop
sfp
lrv
lra
js

function22:
cfp
lra
lhp
push 4
lhp
sw
push 1
lhp
add
shp
push 4
lhp
sw
push 1
lhp
add
shp
push 0
lhp
sw
push 1
lhp
add
shp
push 0
lhp
sw
push 1
lhp
add
shp
push 6
srv
pop
sra
pop
pop
pop
pop
pop
sfp
lrv
lra
js

function23:
cfp
lra
push 25
lfp
push 5
push 5
lfp
push 3
add
lw
push 3
add
lw
lfp
push 3
add
lw
push 2
add
lw
lfp
push 3
add
lw
push 1
add
lw
lfp
push 3
add
lw
push 0
add
lw
lfp
lw
push function5
js
srv
pop
sra
pop
pop
pop
pop
sfp
lrv
lra
js
