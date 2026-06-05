/**
 * 情感粒子组件 - Heart Particles
 * 核心职责：在虚拟拥抱场景提供温暖的视觉反馈
 * 技术要点：
 * 1. 使用Three.js创建爱心形状粒子系统
 * 2. 优化内存，确保低端机型流畅运行
 * 3. 提供accelerate()方法供拥抱交互调用
 */

import * as THREE from 'three-platformize'

// 组件实例属性类型扩展
interface IHeartParticlesComponent {
  scene?: THREE.Scene | null
  camera?: THREE.OrthographicCamera | null
  renderer?: THREE.WebGLRenderer | null
  particles?: THREE.Points | null
  velocities?: number[]
  isAccelerating?: boolean
  accelerationFactor?: number
  animationId?: number
  properties: {
    width: number
    height: number
  }
  data: {
    canvasId: string
  }
  selectComponent: (selector: string) => any
  createSelectorQuery: () => WechatMiniprogram.SelectorQuery
  initThreeJS: () => void
  createParticles: () => void
  animate: () => void
  updateParticles: () => void
  accelerate: () => void
  decelerate: () => void
  dispose: () => void
}

Component({
  properties: {
    width: {
      type: Number,
      value: 375
    },
    height: {
      type: Number,
      value: 667
    }
  },

  data: {
    canvasId: 'heart-particles-canvas'
  },

  lifetimes: {
    attached(this: IHeartParticlesComponent) {
      this.initThreeJS()
    },

    detached(this: IHeartParticlesComponent) {
      // 性能要求：必须清理资源，防止内存泄漏
      this.dispose()
    }
  },

  methods: {
    /**
     * 初始化Three.js场景
     */
    initThreeJS(this: IHeartParticlesComponent) {
      // 小程序环境下，使用 createSelectorQuery 获取 canvas
      const query = this.createSelectorQuery()
      query.select('#' + this.data.canvasId)
        .fields({ node: true, size: true })
        .exec((res) => {
          if (!res || !res[0] || !res[0].node) {
            console.error('Canvas 节点未找到，跳过 Three.js 初始化')
            return
          }

          const canvas = res[0].node
          
          try {
            // 创建场景
            this.scene = new THREE.Scene()

            // 使用正交相机（比透视相机更省资源）
            const aspect = this.properties.width / this.properties.height
            this.camera = new THREE.OrthographicCamera(
              -aspect, aspect, 1, -1, 0.1, 1000
            )
            this.camera.position.z = 5

            // 创建渲染器
            this.renderer = new THREE.WebGLRenderer({
              canvas: canvas,
              antialias: false, // 关闭抗锯齿以提升性能
              alpha: true       // 支持透明背景
            })
            this.renderer.setSize(this.properties.width, this.properties.height)
            this.renderer.setClearColor(0x000000, 0) // 透明背景

            // 创建粒子系统
            this.createParticles()

            // 启动动画循环
            this.animate()
          } catch (error) {
            console.error('Three.js 初始化失败:', error)
          }
        })
    },

    /**
     * 创建爱心粒子系统
     */
    createParticles(this: IHeartParticlesComponent) {
      const particleCount = 25 // 25个粒子（平衡视觉效果和性能）
      const geometry = new THREE.BufferGeometry()
      const positions: number[] = []
      const colors: number[] = []
      const velocities: number[] = []

      // 暖色调色板（粉红、橙红、金黄）
      const warmColors = [
        new THREE.Color(0xff6b9d), // 粉红
        new THREE.Color(0xff8c69), // 橙红
        new THREE.Color(0xffb347), // 金黄
        new THREE.Color(0xff9a8b)  // 淡红
      ]

      for (let i = 0; i < particleCount; i++) {
        // 随机初始位置
        positions.push(
          (Math.random() - 0.5) * 3,  // x
          Math.random() * 3 + 1,       // y（从上方开始）
          (Math.random() - 0.5) * 0.5  // z
        )

        // 随机颜色
        const color = warmColors[Math.floor(Math.random() * warmColors.length)]
        colors.push(color.r, color.g, color.b)

        // 下落速度（随机）
        velocities.push(
          (Math.random() - 0.5) * 0.01, // x方向漂移
          -0.005 - Math.random() * 0.01, // y方向下落
          0                              // z方向不变
        )
      }

      geometry.setAttribute('position', new THREE.Float32BufferAttribute(positions, 3))
      geometry.setAttribute('color', new THREE.Float32BufferAttribute(colors, 3))
      
      // 存储速度信息（用于动画）
      this.velocities = velocities

      // 创建粒子材质
      const material = new THREE.PointsMaterial({
        size: 0.15,
        vertexColors: true,
        transparent: true,
        opacity: 0.8,
        blending: THREE.AdditiveBlending, // 叠加混合模式，更梦幻
        sizeAttenuation: true
      })

      // 创建粒子系统
      this.particles = new THREE.Points(geometry, material)
      if (this.scene) {
        this.scene.add(this.particles)
      }

      // 初始化状态
      this.isAccelerating = false
      this.accelerationFactor = 1.0
    },

    /**
     * 动画循环
     */
    animate(this: IHeartParticlesComponent) {
      if (!this.renderer || !this.scene || !this.camera) return

      this.animationId = requestAnimationFrame(() => this.animate())

      // 更新粒子位置
      this.updateParticles()

      // 渲染场景
      this.renderer.render(this.scene, this.camera)
    },

    /**
     * 更新粒子位置
     */
    updateParticles(this: IHeartParticlesComponent) {
      if (!this.particles || !this.velocities || this.accelerationFactor === undefined) return

      const positions = this.particles.geometry.attributes.position.array as number[]
      const time = Date.now() * 0.001 // 时间因子

      for (let i = 0; i < positions.length; i += 3) {
        // 下落运动
        positions[i + 1] += this.velocities[i + 1] * this.accelerationFactor

        // 左右摇摆（使用sin函数）
        positions[i] += Math.sin(time + i) * 0.001

        // 边界检测：粒子掉出屏幕后，重新从顶部生成
        if (positions[i + 1] < -2) {
          positions[i + 1] = 2                      // 重置到顶部
          positions[i] = (Math.random() - 0.5) * 3  // 随机x位置
        }
      }

      this.particles.geometry.attributes.position.needsUpdate = true

      // 加速时，颜色趋向金黄色
      if (this.isAccelerating) {
        const colors = this.particles.geometry.attributes.color.array as number[]
        const targetColor = new THREE.Color(0xffd700) // 金黄色

        for (let i = 0; i < colors.length; i += 3) {
          colors[i] += (targetColor.r - colors[i]) * 0.05
          colors[i + 1] += (targetColor.g - colors[i + 1]) * 0.05
          colors[i + 2] += (targetColor.b - colors[i + 2]) * 0.05
        }

        this.particles.geometry.attributes.color.needsUpdate = true
      }
    },

    /**
     * 加速粒子下落（拥抱时调用）
     * 供外部调用的接口方法
     */
    accelerate(this: IHeartParticlesComponent) {
      this.isAccelerating = true
      this.accelerationFactor = 3.0 // 加速至3倍速度

      console.log('粒子加速 - 拥抱反馈')
    },

    /**
     * 恢复正常速度
     */
    decelerate(this: IHeartParticlesComponent) {
      this.isAccelerating = false
      this.accelerationFactor = 1.0

      console.log('粒子恢复正常速度')
    },

    /**
     * 清理资源（防止内存泄漏）
     */
    dispose(this: IHeartParticlesComponent) {
      console.log('清理Three.js资源')

      // 取消动画循环
      if (this.animationId) {
        cancelAnimationFrame(this.animationId)
      }

      // 释放几何体
      if (this.particles && this.particles.geometry) {
        this.particles.geometry.dispose()
      }

      // 释放材质
      if (this.particles && this.particles.material) {
        (this.particles.material as THREE.Material).dispose()
      }

      // 释放渲染器（检查方法是否存在）
      if (this.renderer && typeof this.renderer.dispose === 'function') {
        try {
          this.renderer.dispose()
        } catch (error) {
          console.warn('渲染器释放失败:', error)
        }
      }

      // 清空引用
      this.scene = null
      this.camera = null
      this.renderer = null
      this.particles = null
    }
  }
})
