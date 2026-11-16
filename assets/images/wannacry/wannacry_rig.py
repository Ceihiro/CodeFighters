
from PIL import Image, ImageDraw
import random

# Palette
RR = (255,0,64)
RDR = (170,0,48)
WH = (255,255,255)
BG = (255,215,0)
DG = (170,136,0)
CB = (74,85,104)
CH = (106,117,136)
GB = (10,10,10)
SB = (5,5,5)
BK = (0,0,0)
EG = (255,238,136)
SH = (255,170,170)
DS = (136,0,32)
BP = (0,0,170)
DE = (51,51,0)
PG = (0,204,68)

def new_img(w,h):
    return Image.new('RGBA',(w,h),(0,0,0,0))

def outline(img):
    w,h = img.size
    ol = new_img(w,h)
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

def draw_wannacry(img, pose):
    w,h = img.size
    cx = w//2
    bl = h-2
    d = ImageDraw.Draw(img)

    tcx = cx + pose.get('tcx',0)
    tcy = 34 + pose.get('tcy',0)
    tw = pose.get('tw',36)
    th = pose.get('th',28)
    tilt = pose.get('tilt',0)
    bright = pose.get('bright',1.0)

    screen_col = RR if bright>=1.0 else (SH if bright>1.2 else (DS if bright<0.7 else RR))
    if bright>1.5: screen_col = (255,80,100)

    tx1, ty1 = tcx - tw//2, tcy - th//2
    tx2, ty2 = tcx + tw//2, tcy + th//2
    tilt_off = int(tilt * 0.3)

    d.polygon([(tx1+4,ty1+tilt_off),(tx2-4,ty1-tilt_off),(tx2,ty1+4-tilt_off),
               (tx2,ty2),(tx1,ty2),(tx1,ty1+4+tilt_off)], fill=screen_col)
    d.polygon([(tx1+4,ty1+tilt_off),(tx2-4,ty1-tilt_off),(tx2,ty1+4-tilt_off),
               (tx2,ty2),(tx1,ty2),(tx1,ty1+4+tilt_off)], outline=BK, width=1)
    d.polygon([(tx1+5,ty1+tilt_off+1),(tx2-5,ty1-tilt_off+1),(tx2-1,ty1+5-tilt_off),
               (tx2-1,ty2-1),(tx1+1,ty2-1),(tx1+1,ty1+5+tilt_off)], outline=RDR, width=1)
    d.polygon([(tx1+5,ty1+tilt_off+1),(tx1+8,ty1+tilt_off+2),(tx1+8,ty2-1),(tx1+5,ty2-1)], fill=(255,60,100,180))

    shift = pose.get('txt_shift',0)
    lines = pose.get('lines',[(12,4),(8,3),(10,3),(6,2)])
    ly = ty1 + 8 + tilt_off
    for ln_w, ln_h in lines:
        if ly + ln_h < ty2 - 4:
            d.rectangle([tx1+6+shift, ly, tx1+6+shift+ln_w, ly+ln_h], fill=WH)
            ly += ln_h + 2

    if pose.get('x_btn',True):
        d.rectangle([tx2-8, ty1+4-tilt_off, tx2-4, ty1+8-tilt_off], fill=WH)
        d.line([(tx2-7,ty1+5-tilt_off),(tx2-5,ty1+7-tilt_off)], fill=BK, width=1)
        d.line([(tx2-5,ty1+5-tilt_off),(tx2-7,ty1+7-tilt_off)], fill=BK, width=1)

    pbar = pose.get('pbar','empty')
    if pbar=='empty':
        d.rectangle([tx1+8, ty2-6, tx2-8, ty2-3], outline=WH, width=1)
    elif pbar=='full':
        d.rectangle([tx1+8, ty2-6, tx2-8, ty2-3], fill=PG, outline=WH, width=1)

    if pose.get('lock_icon',False):
        lc_x, lc_y = tcx, tcy
        d.rectangle([lc_x-4, lc_y-2, lc_x+4, lc_y+6], fill=WH)
        d.arc([lc_x-4, lc_y-6, lc_x+4, lc_y+2], start=0, end=180, fill=BK, width=2)

    for crack in pose.get('cracks',[]):
        if len(crack) == 4:
            sx,sy,ex,ey = crack
            d.line([(sx,sy),(ex,ey)], fill=WH, width=1)
            d.line([(sx+1,sy),(ex+1,ey)], fill=SH, width=1)

    bsp = pose.get('bsp',None)
    if bsp:
        bx,by = bsp
        d.rectangle([bx,by,bx+10,by+8], fill=BP)

    eye_y = ty1 + 10 + tilt_off
    eye_type = pose.get('eyes','normal')
    eye_glow = pose.get('eye_glow',0)
    ex1, ex2 = tcx - 7, tcx + 7

    for ex in [ex1, ex2]:
        for g in range(eye_glow,0,-1):
            d.ellipse([ex-4-g,eye_y-4-g,ex+4+g,eye_y+4+g], outline=EG, width=1)
        if eye_type=='normal':
            d.ellipse([ex-4,eye_y-4,ex+4,eye_y+4], fill=BG)
            d.ellipse([ex-2,eye_y-2,ex+2,eye_y+2], fill=DG)
            d.rectangle([ex-3,eye_y-3,ex-2,eye_y-2], fill=WH)
        elif eye_type=='narrow':
            d.ellipse([ex-4,eye_y-4,ex+4,eye_y+4], fill=BG)
            d.polygon([(ex-4,eye_y-1),(ex+4,eye_y-3),(ex+4,eye_y+1),(ex-4,eye_y+3)], fill=DG)
            d.rectangle([ex-3,eye_y-3,ex-2,eye_y-2], fill=WH)
        elif eye_type=='bright':
            d.ellipse([ex-4,eye_y-4,ex+4,eye_y+4], fill=BG)
            d.ellipse([ex-3,eye_y-3,ex+3,eye_y+3], fill=WH)
            d.rectangle([ex-2,eye_y-2,ex-1,eye_y-1], fill=BK)
        elif eye_type=='dim':
            d.ellipse([ex-4,eye_y-4,ex+4,eye_y+4], fill=DG)
            d.ellipse([ex-2,eye_y-2,ex+2,eye_y+2], fill=DE)
        elif eye_type=='half_closed':
            d.ellipse([ex-4,eye_y-4,ex+4,eye_y+4], fill=BG)
            d.polygon([(ex-4,eye_y+1),(ex+4,eye_y+1),(ex+4,eye_y+4),(ex-4,eye_y+4)], fill=DG)
        elif eye_type=='white_hot':
            d.ellipse([ex-4,eye_y-4,ex+4,eye_y+4], fill=WH)
            d.ellipse([ex-3,eye_y-3,ex+3,eye_y+3], fill=BG)

    if pose.get('beams',False):
        d.line([(ex1-4,eye_y),(ex1-20,eye_y-4)], fill=BG, width=3)
        d.line([(ex1-4,eye_y),(ex1-20,eye_y-4)], fill=WH, width=1)
        d.line([(ex2-4,eye_y),(ex2-20,eye_y+4)], fill=BG, width=3)
        d.line([(ex2-4,eye_y),(ex2-20,eye_y+4)], fill=WH, width=1)

    def draw_cable(points, is_left):
        for i in range(len(points)-1):
            x1,y1 = points[i]
            x2,y2 = points[i+1]
            segs = 8
            for j in range(segs):
                t1 = j/segs
                t2 = (j+1)/segs
                sx = int(x1+(x2-x1)*t1)
                sy = int(y1+(y2-y1)*t1)
                ex = int(x1+(x2-x1)*t2)
                ey = int(y1+(y2-y1)*t2)
                col = CB if j%2==0 else CH
                d.line([(sx,sy),(ex,ey)], fill=col, width=6)
                d.line([(sx,sy),(ex,ey)], fill=BK, width=1)
        if points:
            ex,ey = points[-1]
            if is_left:
                d.rectangle([ex-4,ey-5,ex+4,ey+5], fill=CB, outline=BK, width=1)
                d.rectangle([ex-3,ey+2,ex-1,ey+4], fill=BG)
                d.rectangle([ex+1,ey+2,ex+3,ey+4], fill=BG)
            else:
                d.polygon([(ex,ey),(ex+10,ey-6),(ex+10,ey+6)], fill=WH)
                d.polygon([(ex,ey),(ex+10,ey-6),(ex+10,ey+6)], outline=BK, width=1)

    lc = pose.get('lcable',[(tcx-14,ty1+8),(tcx-18,ty1+30),(tcx-16,ty1+50),(tcx-14,ty1+65)])
    rc = pose.get('rcable',[(tcx+14,ty1+8),(tcx+20,ty1+25),(tcx+18,ty1+40),(tcx+16,ty1+50)])
    draw_cable(lc, True)
    draw_cable(rc, False)

    for ml in pose.get('motion_lines',[]):
        x,y,l = ml
        d.line([(x,y),(x-l,y)], fill=RR, width=1)

    for ip in pose.get('impact_px',[]):
        x,y,c = ip
        d.rectangle([x,y,x+1,y+1], fill=c)

    if pose.get('data_exp',False):
        for _ in range(25):
            rx = tcx + random.randint(-40,10)
            ry = tcy + random.randint(-30,30)
            c = random.choice([RR,WH,BG])
            d.rectangle([rx,ry,rx+3,ry+3], fill=c)

    tendrils = pose.get('tendrils',[
        [(tcx-10,ty2),(tcx-14,ty2+12),(tcx-8,ty2+24),(tcx-12,bl-4)],
        [(tcx-2,ty2),(tcx-4,ty2+14),(tcx,ty2+26),(tcx-2,bl-6)],
        [(tcx+6,ty2),(tcx+10,ty2+10),(tcx+4,ty2+22),(tcx+8,bl-8)],
        [(tcx+12,ty2),(tcx+16,ty2+12),(tcx+10,ty2+20),(tcx+14,bl-10)]
    ])
    for tend in tendrils:
        for i in range(len(tend)-1):
            d.line([tend[i],tend[i+1]], fill=SB, width=4)
            d.line([tend[i],tend[i+1]], fill=BK, width=1)

    coin_seed = pose.get('coin_seed',42)
    random.seed(coin_seed)
    for _ in range(pose.get('coin_count',3)):
        coin_x = tcx + random.randint(-20,20)
        coin_y = ty1 + random.randint(-10,5)
        d.ellipse([coin_x-2,coin_y-2,coin_x+2,coin_y+2], fill=BG, outline=BK, width=1)
        d.line([(coin_x,coin_y-1),(coin_x,coin_y+1)], fill=DG, width=1)

    gseed = pose.get('gseed',42)
    gw = pose.get('gw',12)
    gb = pose.get('gb',6)
    gconc = pose.get('gconc','screen')
    random.seed(gseed)

    for _ in range(gw):
        if gconc=='screen':
            gx = random.randint(tx1, tx2)
            gy = random.randint(ty1, ty2)
        elif gconc=='arm':
            gx = random.randint(tx1-20, tx1+10)
            gy = random.randint(ty1, ty2+20)
        elif gconc=='all':
            gx = random.randint(0,w-1)
            gy = random.randint(0,h-1)
        else:
            gx = random.randint(tx1, tx2)
            gy = random.randint(ty1, ty2)
        if 0<=gx<w and 0<=gy<h:
            d.rectangle([gx,gy,gx+1,gy+1], fill=WH)
    for _ in range(gb):
        if gconc=='screen':
            gx = random.randint(tx1, tx2)
            gy = random.randint(ty1, ty2)
        elif gconc=='arm':
            gx = random.randint(tx1-20, tx1+10)
            gy = random.randint(ty1, ty2+20)
        elif gconc=='all':
            gx = random.randint(0,w-1)
            gy = random.randint(0,h-1)
        else:
            gx = random.randint(tx1, tx2)
            gy = random.randint(ty1, ty2)
        if 0<=gx<w and 0<=gy<h:
            d.rectangle([gx,gy,gx+1,gy+1], fill=GB)

    for frag in pose.get('frags',[]):
        x,y,c = frag
        d.rectangle([x,y,x+2,y+2], fill=c)

    return outline(img)
