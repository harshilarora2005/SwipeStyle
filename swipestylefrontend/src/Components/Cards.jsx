/* eslint-disable no-unused-vars */
import { useCallback, useMemo, useRef, useImperativeHandle, forwardRef } from "react";
import {
    motion,
    useMotionValue,
    useMotionValueEvent,
    useTransform,
} from "motion/react";

const Cards = forwardRef(({
    clothing,
    clothingData,
    setClothingData,
    index,
    onDragPositionChange,
    onSwipeLeft, 
    onSwipeRight, 
},ref) => {
    const {
        altText,
        imageUrl,
        productId,
    } = clothing;

    const x = useMotionValue(0);
    const opacity = useTransform(x, [-150, 0, 150], [0.3, 1, 0.3]);
    const rotateRaw = useTransform(x, [-150, 150], [-18, 18]);
    const motionRef = useRef(null);
    const isFrontCard = useMemo(() => {
        return productId === clothingData[clothingData.length - 1]?.productId;
    }, [productId, clothingData]);

    const staticOffset = useMemo(() => {
        return isFrontCard ? 0 : index % 2 === 0 ? -6 : 6;
    }, [isFrontCard, index]);

    const rotate = useTransform(rotateRaw, (latest) => `${latest + staticOffset}deg`);
    const animateSwipe = useCallback((direction) => {
        if (motionRef.current) {
            motionRef.current.animate({
                x: direction,
                rotate: direction > 0 ? 18 : -18
            }, {
                type: "spring",
                stiffness: 300,
                damping: 30,
                duration: 1
            });
            setTimeout(() => {
                setClothingData((prev) =>
                    prev.filter((item) => item.productId !== productId)
                );
                if (onDragPositionChange) {
                    onDragPositionChange(0);
                }
            }, 300);
        }
    }, [setClothingData, productId, onDragPositionChange]);

    useImperativeHandle(ref, () => ({
        animateSwipe
    }));
    useMotionValueEvent(x, "change", (latest) => {
        if (onDragPositionChange && isFrontCard) {
        const absLatest = Math.abs(latest);
        const shouldUpdate =
            latest === 0 ||
            (absLatest >= 50 && absLatest % 10 < 1) ||
            (absLatest < 50 && absLatest >= 45) ||
            (absLatest >= 50 && absLatest <= 55);
        if (shouldUpdate) {
            onDragPositionChange(latest);
        }
        }
    });

    const handleDragEnd = useCallback(() => {
        const currentX = x.get();
        if (currentX < -50) {
            if (onSwipeLeft) {
                onSwipeLeft();
            }
            setClothingData((prev) =>
                prev.filter((item) => item.productId !== productId)
            );
        } else if (currentX > 50) {
            if (onSwipeRight) {
                onSwipeRight();
            }
            setClothingData((prev) =>
                prev.filter((item) => item.productId !== productId)
            );
        } else {
            if (onDragPositionChange) {
                onDragPositionChange(0);
            }
        }
    }, [x, setClothingData, productId, onDragPositionChange, onSwipeLeft, onSwipeRight]);

    return (
        <div
        className="min-h-screen flex items-center justify-center relative"
        style={{
            gridRow: 1,
            gridColumn: 1,
            zIndex: isFrontCard ? 10 : index,
        }}
        >
        <motion.div
            ref={motionRef}
            style={{
            x,
            rotate,
            opacity,
            scale: isFrontCard ? 1 : 0.98,
            y: isFrontCard ? 0 : index * 4,
            zIndex: isFrontCard ? 10 : index,
            }}
            animate={{
            scale: isFrontCard ? 1 : 0.98,
            y: isFrontCard ? 0 : index * 4,
            }}
            whileHover={{
            scale: isFrontCard ? 1.02 : 0.99,
            boxShadow: "0 25px 50px -12px rgba(0, 0, 0, 0.25)",
            }}
            whileDrag={{
            scale: 1.05,
            boxShadow: "0 25px 50px -12px rgba(0, 0, 0, 0.4)",
            }}
            transition={{
            type: "spring",
            stiffness: 300,
            damping: 30,
            }}
            drag="x"
            dragConstraints={{ left: 0, right: 0 }}
            onDragEnd={handleDragEnd}
            className="rounded-lg h-96 w-72 hover:cursor-grab active:cursor-grabbing origin-bottom"
        >
            <img
            src={imageUrl}
            alt={altText}
            className="h-full w-full rounded-lg"
            draggable={false}
            />
        </motion.div>
        </div>
    );
});

export default Cards;