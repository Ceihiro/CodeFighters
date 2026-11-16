from PIL import Image, ImageDraw
import os, random

OUT = 'images/ui/screens'
os.makedirs(OUT, exist_ok=True)

PAL = {
    'cyan': (0,255,255), 'dcyan': (0,136,136),
    'gold': (255,215,0), 'dgold': (170,136,0),
    'red': (255,0,64), 'dred': (170,0,48),
    'dgry': (42,42,42), 'black': (26,26,26),
    'white': (255,255,255), 'navy': (11,15,26),
}

FONT = {
    'A': [0b01110,0b10001,0b10001,0b11111,0b10001,0b10001,0b10001],
    'B': [0b11110,0b10001,0b10001,0b11110,0b10001,0b10001,0b11110],
    'C': [0b01110,0b10001,0b10000,0b10000,0b10000,0b10001,0b01110],
    'D': [0b11110,0b10001,0b10001,0b10001,0b10001,0b10001,0b11110],
    'E': [0b11111,0b10000,0b10000,0b11110,0b10000,0b10000,0b11111],
    'F': [0b11111,0b10000,0b10000,0b11110,0b10000,0b10000,0b10000],
    'G': [0b01110,0b10001,0b10000,0b10011,0b10001,0b10001,0b01110],
    'H': [0b10001,0b10001,0b10001,0b11111,0b10001,0b10001,0b10001],
    'I': [0b01110,0b00100,0b00100,0b00100,0b00100,0b00100,0b01110],
    'J': [0b00001,0b00001,0b00001,0b00001,0b10001,0b10001,0b01110],
    'K': [0b10001,0b10010,0b10100,0b11000,0b10100,0b10010,0b10001],
    'L': [0b10000,0b10000,0b10000,0b10000,0b10000,0b10000,0b11111],
    'M': [0b10001,0b11011,0b10101,0b10101,0b10001,0b10001,0b10001],
    'N': [0b10001,0b11001,0b10101,0b10011,0b10001,0b10001,0b10001],
    'O': [0b01110,0b10001,0b10001,0b10001,0b10001,0b10001,0b01110],
    'P': [0b11110,0b10001,0b10001,0b11110,0b10000,0b10000,0b10000],
    'Q': [0b01110,0b10001,0b10001,0b10001,0b10101,0b10010,0b01101],
    'R': [0b11110,0b10001,0b10001,0b11110,0b10100,0b10010,0b10001],
    'S': [0b01111,0b10000,0b10000,0b01110,0b00001,0b00001,0b11110],
    'T': [0b11111,0b00100,0b00100,0b00100,0b00100,0b00100,0b00100],
    'U': [0b10001,0b10001,0b10001,0b10001,0b10001,0b10001,0b01110],
    'V': [0b10001,0b10001,0b10001,0b10001,0b10001,0b01010,0b00100],
    'W': [0b10001,0b10001,0b10001,0b10101,0b10101,0b10101,0b01010],
    'X': [0b10001,0b10001,0b01010,0b00100,0b01010,0b10001,0b10001],
    'Y': [0b10001,0b10001,0b01010,0b00100,0b00100,0b00100,0b00100],
    'Z': [0b11111,0b00001,0b00010,0b00100,0b01000,0b10000,0b11111],
    '0': [0b01110,0b10011,0b10101,0b10101,0b10101,0b11001,0b01110],
    '1': [0b00100,0b01100,0b00100,0b00100,0b00100,0b00100,0b01110],
    '2': [0b01110,0b10001,0b00001,0b00010,0b00100,0b01000,0b11111],
    '3': [0b11111,0b00010,0b00100,0b01110,0b00001,0b10001,0b01110],
    '4': [0b00010,0b00110,0b01010,0b10010,0b11111,0b00010,0b00010],
    '5': [0b11111,0b10000,0b11110,0b00001,0b00001,0b10001,0b01110],
    '6': [0b01110,0b10001,0b10000,0b11110,0b10001,0b10001,0b01110],
    '7': [0b11111,0b00001,0b00010,0b00100,0b01000,0b01000,0b01000],
    '8': [0b01110,0b10001,0b10001,0b01110,0b10001,0b10001,0b01110],
    '9': [0b01110,0b10001,0b10001,0b01111,0b00001,0b10001,0b01110],
    '<': [0b00010,0b00100,0b01000,0b10000,0b01000,0b00100,0b00010],
    '>': [0b01000,0b00100,0b00010,0b00001,0b00010,0b00100,0b01000],
    '/': [0b00001,0b00010,0b00100,0b01000,0b10000,0b00000,0b00000],
    ' ': [0b00000,0b00000,0b00000,0b00000,0b00000,0b00000,0b00000],
}

def draw_char(img, char, x, y, color, shadow_color, outline=True):
    d = ImageDraw.Draw(img)
    bitmap = FONT.get(char, FONT[' '])
    pixels = set()
    for row_idx, row in enumerate(bitmap):
        for col_idx in range(5):
            if row & (1 << (4 - col_idx)):
                pixels.add((x + col_idx, y + row_idx))
    if shadow_color:
        for px, py in pixels:
            d.rectangle([px+2, py+2, px+2, py+2], fill=shadow_color)
    if outline:
        for px, py in pixels:
            for dy in (-1, 0, 1):
                for dx in (-1, 0, 1):
                    if dx == 0 and dy == 0: continue
                    nx, ny = px + dx, py + dy
                    if (nx, ny) not in pixels:
                        d.rectangle([nx, ny, nx, ny], fill=PAL['black'])
    for px, py in pixels:
        d.rectangle([px, py, px, py], fill=color)
    for px, py in pixels:
        if (px, py - 1) not in pixels:
            d.rectangle([px, py, px, py], fill=PAL['white'])
    return x + 6

def draw_text(img, text, x, y, color, shadow_color, center=True):
    w = len(text) * 6 - 1
    if center:
        x = x - w // 2
    for c in text:
        x = draw_char(img, c, x, y, color, shadow_color)
    return x

def make_title_logo():
    base = Image.new('RGBA', (100, 25), (0, 0, 0, 0))
    random.seed(42)
    d = ImageDraw.Draw(base)
    for _ in range(8):
        gx = random.randint(2, 95)
        gy = random.randint(2, 22)
        d.rectangle([gx, gy, gx, gy], fill=random.choice([PAL['cyan'], PAL['red']]))
    draw_text(base, '<', 2, 9, PAL['cyan'], PAL['dcyan'], center=False)
    draw_text(base, '>', 93, 9, PAL['cyan'], PAL['dcyan'], center=False)
    draw_text(base, 'CODE', 50, 3, PAL['cyan'], PAL['dcyan'], center=True)
    draw_text(base, 'FIGHTERS', 50, 14, PAL['gold'], PAL['dgold'], center=True)
    return base.resize((400, 100), Image.NEAREST)

def make_title_logo_large():
    base = Image.new('RGBA', (100, 25), (0, 0, 0, 0))
    random.seed(42)
    d = ImageDraw.Draw(base)
    for _ in range(8):
        gx = random.randint(2, 95)
        gy = random.randint(2, 22)
        d.rectangle([gx, gy, gx, gy], fill=random.choice([PAL['cyan'], PAL['red']]))
    draw_text(base, '<', 2, 9, PAL['cyan'], PAL['dcyan'], center=False)
    draw_text(base, '>', 93, 9, PAL['cyan'], PAL['dcyan'], center=False)
    draw_text(base, 'CODE', 50, 3, PAL['cyan'], PAL['dcyan'], center=True)
    draw_text(base, 'FIGHTERS', 50, 14, PAL['gold'], PAL['dgold'], center=True)
    large = base.resize((800, 200), Image.NEAREST)
    d2 = ImageDraw.Draw(large)
    random.seed(123)
    for _ in range(20):
        gx = random.randint(10, 780)
        gy = random.randint(10, 180)
        d2.rectangle([gx, gy, gx+1, gy+1], fill=random.choice([PAL['cyan'], PAL['red'], PAL['white']]))
    return large

def make_title_logo_icon():
    img = Image.new('RGBA', (128, 128), PAL['navy'])
    d = ImageDraw.Draw(img)
    d.rectangle([0, 0, 127, 127], outline=PAL['cyan'], width=4)
    notch = 8
    d.polygon([(0,0),(notch,0),(0,notch)], fill=PAL['navy'])
    d.line([(notch,0),(0,notch)], fill=PAL['cyan'], width=1)
    d.polygon([(128,0),(128-notch,0),(128,notch)], fill=PAL['navy'])
    d.line([(128-notch,0),(128,notch)], fill=PAL['cyan'], width=1)
    d.polygon([(0,128),(notch,128),(0,128-notch)], fill=PAL['navy'])
    d.line([(notch,128),(0,128-notch)], fill=PAL['cyan'], width=1)
    d.polygon([(128,128),(128-notch,128),(128,128-notch)], fill=PAL['navy'])
    d.line([(128-notch,128),(128,128-notch)], fill=PAL['cyan'], width=1)
    base = Image.new('RGBA', (32, 32), (0, 0, 0, 0))
    draw_text(base, 'C', 2, 2, PAL['cyan'], PAL['dcyan'], center=False)
    draw_text(base, 'F', 14, 2, PAL['gold'], PAL['dgold'], center=False)
    draw_text(base, '</>', 16, 18, PAL['white'], PAL['dgry'], center=True)
    cf = base.resize((64, 64), Image.NEAREST)
    img.paste(cf, (32, 28), cf)
    return img

if __name__ == '__main__':
    make_title_logo().save(f'{OUT}/title_logo.png')
    make_title_logo_large().save(f'{OUT}/title_logo_large.png')
    make_title_logo_icon().save(f'{OUT}/title_logo_icon.png')
    print("Done!")
