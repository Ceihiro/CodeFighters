
from PIL import Image, ImageDraw
import os

# Palette
FW = (255,255,255)
FG = (160,160,160)
HC = (0,255,255)
HDC = (0,170,170)
HPB = (26,26,26)
HPA = (0,204,204)
BK = (0,0,0)
NP = (255,170,170)
PHB = (51,51,51)
PHS = (102,255,255)
GL = (170,255,255)
IY = (255,255,0)
HR = (255,0,64)
WH = (255,255,255)

def outline(img):
    w,h = img.size
    ol = Image.new('RGBA',(w,h),(0,0,0,0))
    op = ol.load()
    px = img.load()
    for y in range(h):
        for x in range(w):
            if px[x,y][3]==0:
                for dy in (-1,0,1):
                    for dx in (-1,0,1):
                        if dx==0 and dy==0: continue
                        nx,ny = x+dx,y+dy
                        if 0<=nx<w and 0<=ny<h and px[nx,ny][3]>0:
                            op[x,y] = (0,0,0,255)
                            break
                    else: continue
                    break
    return Image.alpha_composite(ol,img)

def new_img(w,h):
    return Image.new('RGBA',(w,h),(0,0,0,0))

def draw_byte(img, pose):
    w,h = img.size
    cx = w//2
    bl = h-2
    d = ImageDraw.Draw(img)

    bx = cx + pose.get('box',0)
    by = bl - 22 + pose.get('boy',0)
    lean = pose.get('lean',0)
    hx = bx + pose.get('hox',0) + lean
    hy = by - 24 + pose.get('hoy',0)
    hr = pose.get('hr',9)

    tail = pose.get('tail',[(bx-10,by+6),(bx-18,by+18),(bx-8,by+28),(bx-14,bl-8)])
    for i in range(len(tail)-1):
        d.line([tail[i],tail[i+1]],fill=FW,width=3)
    if len(tail)>=2:
        tx,ty = tail[-2]
        d.ellipse([tx-2,ty-2,tx+2,ty+2],fill=FG)

    la = pose.get('la',[(bx-8,by-4),(bx-12,by+6),(bx-14,by+16)])
    for i in range(len(la)-1):
        d.line([la[i],la[i+1]],fill=HC,width=5)
        d.line([la[i],la[i+1]],fill=HDC,width=1)
    if len(la)>=2:
        d.ellipse([la[1][0]-2,la[1][1]-2,la[1][0]+2,la[1][1]+2],fill=FG)
        d.ellipse([la[2][0]-2,la[2][1]-2,la[2][0]+2,la[2][1]+2],fill=FG)
    if la:
        d.ellipse([la[-1][0]-3,la[-1][1]-3,la[-1][0]+3,la[-1][1]+3],fill=FW)

    ll = pose.get('ll',[(bx-6,by+8),(bx-8,by+22),(bx-10,bl)])
    for i in range(len(ll)-1):
        d.line([ll[i],ll[i+1]],fill=FW,width=6)
    if len(ll)>=2:
        d.ellipse([ll[1][0]-2,ll[1][1]-2,ll[1][0]+2,ll[1][1]+2],fill=FG)

    bw,bh = 20,16
    bty = by - bh//2
    bby = by + bh//2
    blf = bx - bw//2
    brt = bx + bw//2
    btl = bx - 8 + lean//2
    btr = bx + 8 + lean//2
    d.polygon([(btl,bty),(btr,bty),(brt,bby),(blf,bby)],fill=HC)
    d.polygon([(btl,bty+3),(btr,bty+3),(brt-2,bby),(blf+2,bby)],fill=HDC)
    for wx in range(int(blf),int(brt),3):
        d.rectangle([wx,bby,wx+1,bby+1],fill=HDC)
    d.rectangle([bx-3,by+2,bx+3,by+8],fill=HDC)
    d.line([(bx-4,bty+4),(bx-4,bty+10)],fill=HC,width=1)
    d.line([(bx+4,bty+4),(bx+4,bty+10)],fill=HC,width=1)
    d.ellipse([blf+2,bty+6,blf+6,bty+10],fill=FG)

    hc = pose.get('hood',(hx,hy))
    hox,hoy = hc
    d.polygon([(hox-12,hoy+8),(hox-10,hoy-10),(hox+10,hoy-10),(hox+12,hoy+8),(hox+8,hoy+4),(hox-8,hoy+4)],fill=HC)
    d.polygon([(hox-10,hoy+6),(hox-8,hoy-6),(hox+8,hoy-6),(hox+10,hoy+6)],fill=HDC)

    rl = pose.get('rl',[(bx+6,by+8),(bx+8,by+22),(bx+10,bl)])
    for i in range(len(rl)-1):
        d.line([rl[i],rl[i+1]],fill=FW,width=6)

    ra = pose.get('ra',[(bx+8,by-4),(bx+12,by+6),(bx+14,by+16)])
    for i in range(len(ra)-1):
        d.line([ra[i],ra[i+1]],fill=HC,width=5)
    if len(ra)>=2:
        d.ellipse([ra[2][0]-2,ra[2][1]-2,ra[2][0]+2,ra[2][1]+2],fill=FG)
    if ra:
        d.ellipse([ra[-1][0]-3,ra[-1][1]-3,ra[-1][0]+3,ra[-1][1]+3],fill=FW)

    if pose.get('phone',True) and ra:
        hand = ra[-1]
        pa = pose.get('pang',0)
        if pa==0:
            d.rectangle([hand[0]-3,hand[1]-8,hand[0]+3,hand[1]+2],fill=PHB)
            glow = pose.get('pglow',0)
            sc = WH if glow>2 else (GL if glow>0 else PHS)
            d.rectangle([hand[0]-2,hand[1]-6,hand[0]+2,hand[1]],fill=sc)
            d.rectangle([hand[0]-4,hand[1]-6,hand[0]-3,hand[1]-2],fill=FW)
            d.rectangle([hand[0]+3,hand[1]-6,hand[0]+4,hand[1]-2],fill=FW)
        elif pa==45:
            d.polygon([(hand[0]-2,hand[1]-8),(hand[0]+4,hand[1]-6),(hand[0]+2,hand[1]+4),(hand[0]-4,hand[1]+2)],fill=PHB)
            d.polygon([(hand[0]-1,hand[1]-6),(hand[0]+3,hand[1]-5),(hand[0]+1,hand[1]+2),(hand[0]-3,hand[1]+1)],fill=PHS)
        elif pa==90:
            d.rectangle([hand[0]-2,hand[1]-3,hand[0]+10,hand[1]+3],fill=PHB)
            d.rectangle([hand[0],hand[1]-2,hand[0]+8,hand[1]+2],fill=WH)
            d.rectangle([hand[0]+8,hand[1]-2,hand[0]+10,hand[1]+2],fill=GL)

    d.ellipse([hx-hr,hy-hr,hx+hr,hy+hr],fill=FW)
    spots = pose.get('hspots',[(hx-4,hy-4),(hx+5,hy-2),(hx-2,hy+5),(hx+3,hy+4)])
    for sx,sy in spots:
        d.ellipse([sx-2,sy-2,sx+2,sy+2],fill=FG)

    elo,ero = pose.get('elo',(-6,-10)), pose.get('ero',(6,-10))
    d.polygon([(hx+elo[0],hy+elo[1]),(hx+elo[0]-4,hy+elo[1]-8),(hx+elo[0]+3,hy+elo[1]-2)],fill=FW)
    d.polygon([(hx+elo[0],hy+elo[1]),(hx+elo[0]-3,hy+elo[1]-6),(hx+elo[0]+2,hy+elo[1]-2)],fill=FG)
    d.polygon([(hx+ero[0],hy+ero[1]),(hx+ero[0]+4,hy+ero[1]-8),(hx+ero[0]-3,hy+ero[1]-2)],fill=FW)
    d.polygon([(hx+ero[0],hy+ero[1]),(hx+ero[0]+3,hy+elo[1]-6),(hx+ero[0]-2,hy+ero[1]-2)],fill=FG)

    ht = pose.get('hpt',0)
    d.ellipse([hx-14+ht,hy-6,hx-6+ht,hy+6],fill=HPB)
    d.arc([hx-14+ht,hy-6,hx-6+ht,hy+6], start=0, end=180, fill=HPA, width=1)
    d.ellipse([hx+6+ht,hy-6,hx+14+ht,hy+6],fill=HPB)
    d.arc([hx+6+ht,hy-6,hx+14+ht,hy+6], start=0, end=180, fill=HPA, width=1)
    d.arc([hx-10+ht,hy-18,hx+10+ht,hy+2], start=0, end=180, fill=HPB, width=3)
    d.rectangle([hx-1+ht,hy-16,hx+1+ht,hy-14],fill=HPA)

    es = pose.get('eyes','open')
    ey = hy + 2
    lex,rex = hx-4,hx+4
    if es=='open':
        d.ellipse([lex-2,ey-2,lex+2,ey+2],fill=BK)
        d.ellipse([rex-2,ey-2,rex+2,ey+2],fill=BK)
        d.rectangle([lex-1,ey-1,lex,ey],fill=WH)
        d.rectangle([rex,ey-1,rex+1,ey],fill=WH)
    elif es=='blink':
        d.line([lex-2,ey,lex+2,ey],fill=BK,width=1)
        d.line([rex-2,ey,rex+2,ey],fill=BK,width=1)
    elif es=='slit':
        d.line([lex-2,ey,lex+2,ey],fill=BK,width=2)
        d.line([rex-2,ey,rex+2,ey],fill=BK,width=2)
    elif es=='wide':
        d.ellipse([lex-3,ey-3,lex+3,ey+3],fill=BK)
        d.ellipse([rex-3,ey-3,rex+3,ey+3],fill=BK)
        d.rectangle([lex-2,ey-2,lex,ey],fill=WH)
        d.rectangle([rex,ey-2,rex+2,ey],fill=WH)
    elif es=='shock':
        d.ellipse([lex-3,ey-3,lex+3,ey+3],fill=WH)
        d.ellipse([rex-3,ey-3,rex+3,ey+3],fill=WH)
        d.ellipse([lex-1,ey-1,lex+1,ey+1],fill=BK)
        d.ellipse([rex-1,ey-1,rex+1,ey+1],fill=BK)
    elif es=='happy':
        d.arc([lex-3,ey-3,lex+3,ey+3], start=0, end=180, fill=BK, width=2)
        d.arc([rex-3,ey-3,rex+3,ey+3], start=0, end=180, fill=BK, width=2)
    elif es=='closed':
        d.arc([lex-3,ey-3,lex+3,ey+3], start=180, end=360, fill=BK, width=2)
        d.arc([rex-3,ey-3,rex+3,ey+3], start=180, end=360, fill=BK, width=2)

    d.rectangle([hx-1,hy+4,hx+1,hy+6],fill=NP)

    mouth = pose.get('mouth','smile')
    if mouth=='smile':
        d.arc([hx-3,hy+4,hx+3,hy+10], start=0, end=180, fill=BK, width=1)
    elif mouth=='fierce':
        d.line([hx-3,hy+7,hx+3,hy+7],fill=BK,width=2)
        d.line([hx-2,hy+8,hx+2,hy+8],fill=BK,width=1)
    elif mouth=='o':
        d.ellipse([hx-2,hy+5,hx+2,hy+9],fill=BK)
    elif mouth=='big_smile':
        d.arc([hx-4,hy+3,hx+4,hy+11], start=0, end=180, fill=BK, width=2)
    elif mouth=='open':
        d.rectangle([hx-2,hy+5,hx+2,hy+10],fill=BK)

    d.line([hx-8,hy+4,hx-12,hy+3],fill=FG,width=1)
    d.line([hx-8,hy+6,hx-11,hy+7],fill=FG,width=1)
    d.line([hx+8,hy+4,hx+12,hy+3],fill=FG,width=1)
    d.line([hx+8,hy+6,hx+11,hy+7],fill=FG,width=1)

    d.ellipse([hx-8,ey-3,hx-2,ey+3],outline=BK,width=1)
    d.ellipse([hx+2,ey-3,hx+8,ey+3],outline=BK,width=1)
    d.line([hx-2,ey,hx+2,ey],fill=BK,width=1)
    d.rectangle([hx-7,ey-2,hx-6,ey-1],fill=WH)
    d.rectangle([hx+3,ey-2,hx+4,ey-1],fill=WH)

    for eff in pose.get('fx',[]):
        t,x,y = eff['t'],eff['x'],eff['y']
        if t=='line':
            d.line([(x,y),(x-eff['l'],y)],fill=eff['c'],width=1)
        elif t=='star':
            s = eff['s']
            d.line([(x,y-s),(x,y+s)],fill=eff['c'],width=2)
            d.line([(x-s,y),(x+s,y)],fill=eff['c'],width=2)
        elif t=='6star':
            s,c1,c2 = eff['s'],eff['c'],eff.get('c2',eff['c'])
            d.line([(x,y-s),(x,y+s)],fill=c1,width=2)
            d.line([(x-s,y),(x+s,y)],fill=c1,width=2)
            d.line([(x-int(s*0.7),y-int(s*0.7)),(x+int(s*0.7),y+int(s*0.7))],fill=c2,width=2)
            d.line([(x+int(s*0.7),y-int(s*0.7)),(x-int(s*0.7),y+int(s*0.7))],fill=c2,width=2)
        elif t=='dust':
            for i in range(eff['n']):
                d.rectangle([x+i*3,y+i*2,x+i*3+2,y+i*2+2],fill=eff['c'])
        elif t=='sparkle':
            d.rectangle([x,y,x+1,y+1],fill=eff['c'])
        elif t=='arc':
            d.arc([x-eff['r'],y-eff['r'],x+eff['r'],y+eff['r']], start=eff['a1'], end=eff['a2'], fill=eff['c'], width=2)
        elif t=='flash':
            d.ellipse([x-3,y-3,x+3,y+3],fill=eff['c'])
            d.rectangle([x-1,y-1,x+1,y+1],fill=WH)
        elif t=='impact':
            d.rectangle([x,y,x+2,y+2],fill=HR)
            d.rectangle([x+2,y-1,x+4,y+1],fill=HC)
            d.rectangle([x-1,y+2,x+1,y+4],fill=HC)
        elif t=='muzzle':
            d.ellipse([x-3,y-3,x+3,y+3],fill=GL)
            d.rectangle([x-1,y-1,x+1,y+1],fill=WH)
            for i in range(3):
                d.line([(x+4+i*2,y-2+i),(x+7+i*2,y-3+i)],fill=GL,width=1)
        elif t=='vibe':
            for dx,dy in [(-1,0),(1,0),(0,-1),(0,1)]:
                ov = Image.new('RGBA',img.size,(0,0,0,0))
                od = ImageDraw.Draw(ov)
                od.rectangle([x+dx-3,y+dy-8,x+dx+3,y+dy+2],fill=(*eff['c'][:3],60))
                img = Image.alpha_composite(img,ov)
                d = ImageDraw.Draw(img)
        elif t=='cloud':
            for i in range(5):
                d.ellipse([x+i*4,y+i*2,x+i*4+6,y+i*2+4],fill=eff['c'])

    return outline(img)
